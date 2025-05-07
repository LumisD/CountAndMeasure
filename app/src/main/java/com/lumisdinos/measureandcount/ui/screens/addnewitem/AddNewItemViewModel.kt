package com.lumisdinos.measureandcount.ui.screens.addnewitem

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.NewScreenType
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.toChipboard
import com.lumisdinos.measureandcount.ui.model.toUnionOfChipboards
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.toChipboardUi
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
            val titleTemplate = String.format(
                context.getString(R.string.chipboard_sheet_list_title),
                getCurrentDateTime()
            )
            val newUnion =
                UnionOfChipboardsUI(
                    title = titleTemplate,
                    isFinished = false,
                    createdAt = System.currentTimeMillis()
                ).toUnionOfChipboards()
            val unionId = chipboardRepository.insertUnionOfChipboards(newUnion)

            if (unionId != null) {
                _state.update { currentState ->
                    currentState.copy(
                        titleOfUnion = titleTemplate,
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
                    chipboards
                        .sortedByDescending { it.id }
                        .map {
                            it.toChipboardUi().copy(
                                quantityAsString = it.quantity.toString(),
                                size1AsString = it.size1.toString(),
                                size2AsString = it.size2.toString(),
                                size3AsString = it.size3.toString(),
                                chipboardAsString = getChipboardAsString(it.toChipboardUi())
                            )
                        }
                _state.update { it.copy(createdChipboards = updatedChipboards) }
            }
        }
    }

    fun processIntent(intent: AddNewItemIntent) {
        when (intent) {
            is AddNewItemIntent.TitleOfUnionChanged -> updateUnionTitle(intent.newTitle)

            is AddNewItemIntent.SizeChanged -> updateChipboardSize(
                intent.newSizeAsString,
                intent.dimension
            )

            is AddNewItemIntent.ColorChanged -> updateChipboardColor(
                intent.newColorName,
                intent.newColor
            )

            is AddNewItemIntent.QuantityChanged -> updateChipboardQuantity(intent.newQuantityAsString)

            is AddNewItemIntent.AddChipboardToDb -> addChipboardToDb()

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
            is AddNewItemIntent.SetItemType -> setInitialCharacteristicsOfChipboard(intent.itemType)

            is AddNewItemIntent.AskEditChipboard -> {
                viewModelScope.launch {
                    _effect.send(AddNewItemEffect.ShowEditConfirmationDialog(intent.chipboard))
                }
            }

            is AddNewItemIntent.AskDeleteChipboard -> {
                viewModelScope.launch {
                    _effect.send(AddNewItemEffect.ShowDeleteConfirmationDialog(intent.chipboard))
                }
            }

            is AddNewItemIntent.DeleteChipboardConfirmed -> deleteChipboardFromDb(intent.chipboardId)

            is AddNewItemIntent.EditChipboardConfirmed -> editChipboardInAddAreaAndRemoveFromDb(
                intent.chipboard
            )
        }
    }

    private fun addChipboardToDb() {
        viewModelScope.launch {
            chipboardRepository.insertChipboard(_state.value.newOrEditChipboard.toChipboard())
            _effect.send(AddNewItemEffect.ShowSnackbar(context.getString(R.string.new_item_added)))
        }
        _state.update { currentState ->
            val currentChipboard = currentState.newOrEditChipboard
            val newChipboard = currentChipboard.copy(
                id = 0,
                quantity = 1,
                quantityAsString = "1",
                size1 = 0f,
                size1AsString = "",
                size2 = 0f,
                size2AsString = "",
                size3 = 0f,
                size3AsString = ""
            )
            val newChipboard2 =
                newChipboard.copy(chipboardAsString = getChipboardAsString(newChipboard))
            currentState.copy(
                newOrEditChipboard = newChipboard2,
                isAddButtonAvailable = false
            )
        }
    }

    private fun editChipboardInAddAreaAndRemoveFromDb(chipboard: ChipboardUi) {
        _state.update {
            it.copy(
                isAddAreaOpen = true,
                isAddButtonAvailable = true,
                newOrEditChipboard = chipboard
            )
        }
        viewModelScope.launch {
            chipboardRepository.deleteChipboardById(chipboard.id)
            _effect.send(AddNewItemEffect.FlashAddItemArea)
        }
    }

    private fun deleteChipboardFromDb(chipboardId: Int) {
        viewModelScope.launch {
            chipboardRepository.deleteChipboardById(chipboardId)
            _effect.send(AddNewItemEffect.ShowSnackbar(context.getString(R.string.item_deleted)))
        }
    }

    private fun updateChipboardSize(newSizeAsString: String, dimension: Int) {
        val newSizeAsFloat = newSizeAsString.toFloatOrNull() ?: 0f

        _state.update { currentState ->
            val currentChipboard = currentState.newOrEditChipboard
            val updatedChipboard = when (dimension) {
                1 -> currentChipboard.copy(
                    size1AsString = newSizeAsString,
                    size1 = newSizeAsFloat
                )

                2 -> currentChipboard.copy(
                    size2AsString = newSizeAsString,
                    size2 = newSizeAsFloat
                )

                3 -> currentChipboard.copy(
                    size3AsString = newSizeAsString,
                    size3 = newSizeAsFloat
                )

                else -> currentChipboard
            }
            val updatedChipboard2 =
                updatedChipboard.copy(chipboardAsString = getChipboardAsString(updatedChipboard))
            val setAddButnAvailbl = setAddButtonAvailability(updatedChipboard)
            currentState.copy(
                newOrEditChipboard = updatedChipboard2,
                isAddButtonAvailable = setAddButnAvailbl
            )
        }
    }


    private fun updateUnionTitle(newTitle: String) {
        viewModelScope.launch {
            chipboardRepository.updateUnionOfChipboardsTitle(
                _state.value.newOrEditChipboard.unionId,
                newTitle,
                System.currentTimeMillis()
            )
        }
        _state.update { it.copy(titleOfUnion = newTitle) }
    }

    private fun updateChipboardColor(newColorName: String, newColor: Int) {
        _state.update { currentState ->
            val updatedChipboard = currentState.newOrEditChipboard.copy(
                colorName = newColorName,
                color = newColor
            )
            val updatedChipboard2 =
                updatedChipboard.copy(chipboardAsString = getChipboardAsString(updatedChipboard))
            currentState.copy(
                newOrEditChipboard = updatedChipboard2,
            )
        }
    }

    private fun updateChipboardQuantity(newQuantityAsString: String) {
        val newQuantityAsShort = newQuantityAsString.toShortOrNull() ?: 0
        _state.update { currentState ->
            val updatedChipboard = currentState.newOrEditChipboard.copy(
                quantityAsString = newQuantityAsString,
                quantity = newQuantityAsShort
            )
            val updatedChipboard2 =
                updatedChipboard.copy(chipboardAsString = getChipboardAsString(updatedChipboard))
            val setAddButnAvailbl = setAddButtonAvailability(updatedChipboard)
            currentState.copy(
                newOrEditChipboard = updatedChipboard2,
                isAddButtonAvailable = setAddButnAvailbl
            )
        }
    }

    private fun getChipboardAsString(chipboard: ChipboardUi): String {
        //↑12.5 x 54.0 - 3
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
        builder.append(" - ${chipboard.quantity}")
        return builder.toString()
    }

    private fun setAddButtonAvailability(chipboard: ChipboardUi): Boolean {
        var isAddButtonAvailable = true
        for (i in 1..chipboard.dimensions) {
            when (i) {
                1 -> {
                    if (chipboard.size1 == 0f) isAddButtonAvailable = false
                }

                2 -> {
                    if (chipboard.size2 == 0f) isAddButtonAvailable = false
                }

                3 -> {
                    if (chipboard.size3 == 0f) isAddButtonAvailable = false
                }
            }
            if (chipboard.quantity.toInt() == 0) isAddButtonAvailable = false
        }
        return isAddButtonAvailable
    }


    private fun setInitialCharacteristicsOfChipboard(itemType: NewScreenType) {
        _state.update { currentState ->
            val currentChipboard = currentState.newOrEditChipboard

            val dimensions = minOf(itemType.columnNames.size, 3).toShort()
            val directionColumn = minOf(itemType.directionColumn, 3).toShort()

            val titles = itemType.columnNames.map { context.getString(it) }

            val updatedChipboard = currentChipboard.copy(
                direction = directionColumn,
                dimensions = dimensions,
                title1 = titles.getOrElse(0) { "" },
                title2 = titles.getOrElse(1) { "" },
                title3 = titles.getOrElse(2) { "" }
            )
            val updatedChipboard2 =
                updatedChipboard.copy(chipboardAsString = getChipboardAsString(updatedChipboard))

            currentState.copy(
                newOrEditChipboard = updatedChipboard2,
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