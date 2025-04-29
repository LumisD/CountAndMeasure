package com.lumisdinos.measureandcount.ui.screens.addnewitem

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
import com.lumisdinos.measureandcount.ui.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.NewScreenType
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.utils.getCurrentDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewItemViewModel @Inject constructor(
    private val chipboardRepository: MeasureAndCountRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(AddNewItemState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AddNewItemEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        createNewUnion()
    }

    private fun createNewUnion() {
        viewModelScope.launch {
            val newUnion = UnionOfChipboardsUI(createdAt = System.currentTimeMillis())
            val unionId = chipboardRepository.insertUnionOfChipboards(newUnion)

            if (unionId != null) {
                _state.update { currentState ->
                    val titleTemplate = context.getString(R.string.chipboard_sheet_list_title)
                    currentState.copy(
                        title = String.format(titleTemplate, getCurrentDateTime()),
                        newOrEditChipboard = currentState.newOrEditChipboard.copy(unionId = unionId)
                    )
                }
                observeChipboardsForUnion(unionId)
            } else {
                //TODO
                // Handle error: Could not create a new union
                // You might want to set an error state or show a message to the user
                //_state.update { it.copy(error = "Failed to create a new item. Please try again.") }
            }
        }
    }

    private fun observeChipboardsForUnion(unionId: Int) {
        viewModelScope.launch {
            chipboardRepository.getChipboardsByUnionIdFlow(unionId).collect { chipboards ->
                val updatedChipboards =
                    chipboards.map { it.copy(chipboardAsString = getChipboardAsString(it)) }
                _state.update { it.copy(chipboards = updatedChipboards) }
            }
        }
    }

    fun processIntent(intent: AddNewItemIntent) {
        when (intent) {
            is AddNewItemIntent.TitleChanged -> updateUnionTitle(intent.newTitle)

            is AddNewItemIntent.SizeChanged -> updateChipboardSize(
                intent.newSizeAsString,
                intent.dimension
            )

            is AddNewItemIntent.ColorChanged -> updateColor(intent.newColor)

            is AddNewItemIntent.QuantityChanged -> updateQuantity(intent.newQuantityAsString)

            is AddNewItemIntent.AddChipboard -> addChipboard()

            AddNewItemIntent.ToggleAddAreaVisibility -> {
                _state.update { it.copy(isAddAreaOpen = !it.isAddAreaOpen) }
            }

            AddNewItemIntent.Back -> {
                viewModelScope.launch {
                    _effect.send(AddNewItemEffect.NavigateBack)
                }
                //_state.update { it.copy(navigateBack = true) }
            }
//            AddNewItemIntent.ResetNavigateBack -> {
//                _state.update { it.copy(navigateBack = false) }
//            }
            is AddNewItemIntent.SetItemType -> setChipboardMainData(intent.itemType)
            is AddNewItemIntent.EditChipboard -> {
                _state.update { it.copy(newOrEditChipboard = intent.chipboard) }
            }

            is AddNewItemIntent.DeleteChipboard -> {
                viewModelScope.launch {
                    _effect.send(AddNewItemEffect.ShowDeleteConfirmationDialog(intent.chipboard))
                }
            }

            is AddNewItemIntent.DeleteChipboardConfirmed -> deleteChipboard(intent.chipboardId)
        }
    }

    private fun addChipboard() {
        viewModelScope.launch {
            chipboardRepository.insertChipboard(_state.value.newOrEditChipboard)
            _effect.send(AddNewItemEffect.ShowSnackbar(context.getString(R.string.new_item_added)))
        }
        _state.update { currentState ->
            val currentChipboard = currentState.newOrEditChipboard
            val newChipboard = currentChipboard.copy(
                quantity = 1,
                quantityAsString = "1",
                size1 = 0f,
                size1AsString = "",
                size2 = 0f,
                size2AsString = "",
                size3 = 0f,
                size3AsString = ""
            )
            currentState.copy(
                newOrEditChipboard = newChipboard,
                editingChipboardAsString = getChipboardAsString(newChipboard)
            )
        }
    }

    private fun deleteChipboard(chipboardId: Int) {
        viewModelScope.launch {
            chipboardRepository.deleteChipboardById(chipboardId)
            _effect.send(AddNewItemEffect.ShowSnackbar(context.getString(R.string.item_deleted)))
        }
    }

    private fun updateChipboardSize(newSizeAsString: String, dimension: Int) {
        val newSizeAsFloat = newSizeAsString.toFloatOrNull()

        _state.update { currentState ->
            val currentChipboard = currentState.newOrEditChipboard
            val updatedChipboard = when (dimension) {
                1 -> currentChipboard.copy(
                    size1AsString = newSizeAsString,
                    size1 = newSizeAsFloat ?: currentChipboard.size1
                )

                2 -> currentChipboard.copy(
                    size2AsString = newSizeAsString,
                    size2 = newSizeAsFloat ?: currentChipboard.size2
                )

                3 -> currentChipboard.copy(
                    size3AsString = newSizeAsString,
                    size3 = newSizeAsFloat ?: currentChipboard.size3
                )

                else -> currentChipboard
            }
            currentState.copy(
                newOrEditChipboard = updatedChipboard,
                editingChipboardAsString = getChipboardAsString(updatedChipboard)
            )
        }
    }


    private fun updateUnionTitle(newTitle: String) {
        viewModelScope.launch {
            chipboardRepository.updateUnionOfChipboardsTitle(
                _state.value.newOrEditChipboard.unionId,
                newTitle
            )
        }
        _state.update { it.copy(title = newTitle) }
    }

    private fun updateColor(newColor: String) {
        _state.update { currentState ->
            val updatedChipboard = currentState.newOrEditChipboard.copy(color = newColor)
            currentState.copy(
                newOrEditChipboard = updatedChipboard,
                editingChipboardAsString = getChipboardAsString(updatedChipboard)
            )
        }
    }

    private fun updateQuantity(newQuantityAsString: String) {
        val newQuantityAsShort = newQuantityAsString.toShortOrNull()
        _state.update { currentState ->
            val updatedChipboard = currentState.newOrEditChipboard.copy(
                quantityAsString = newQuantityAsString,
                quantity = newQuantityAsShort ?: currentState.newOrEditChipboard.quantity
            )
            currentState.copy(
                newOrEditChipboard = updatedChipboard,
                editingChipboardAsString = getChipboardAsString(updatedChipboard)
            )
        }
    }

    private fun getChipboardAsString(chipboard: ChipboardUi): String {
        //↑12.5 x 54.0 x Blue - 3qty
        val builder = StringBuilder()
        for (i in 1..chipboard.dimensions) {
            if (chipboard.direction.toInt() == i) {
                builder.append("↑")
            }
            when (i) {
                1 -> builder.append(chipboard.size1)
                2 -> builder.append(chipboard.size2)
                3 -> builder.append(chipboard.size3)
            }
            if (i < chipboard.dimensions) {
                builder.append(" x ")
            }
        }
        if (chipboard.color.isNotEmpty()) {
            builder.append(" ${chipboard.color}")
        }
        builder.append(" - ${chipboard.quantity} qty")
        return builder.toString()
    }


    private fun setChipboardMainData(itemType: NewScreenType) {
        _state.update { currentState ->
            val currentChipboard = currentState.newOrEditChipboard

            val dimensions = minOf(itemType.columnNames.size, 5).toShort()
            val directionColumn = minOf(itemType.directionColumn, 5).toShort()

            val titles = itemType.columnNames.map { context.getString(it) }

            val updatedChipboard = currentChipboard.copy(
                direction = directionColumn,
                dimensions = dimensions,
                title1 = titles.getOrElse(0) { "" },
                title2 = titles.getOrElse(1) { "" },
            )

            currentState.copy(
                newOrEditChipboard = updatedChipboard,
                editingChipboardAsString = getChipboardAsString(updatedChipboard)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            val unionId = state.value.newOrEditChipboard.unionId
            if (chipboardRepository.getChipboardsCountByUnionId(unionId) == 0) {
                chipboardRepository.deleteUnionOfChipboards(unionId)
            }
        }
    }


}