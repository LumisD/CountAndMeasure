package com.lumisdinos.measureandcount.ui.screens.count

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.model.toUnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.screens.count.model.toChipboard
import com.lumisdinos.measureandcount.ui.screens.count.model.toChipboardUi
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
class CountViewModel @Inject constructor(
    private val chipboardRepository: MeasureAndCountRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CountState())
    val state = _state.asStateFlow()

    private val _effect = Channel<CountEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    //private var unionOfChipboards: UnionOfChipboardsUI = UnionOfChipboardsUI()


    fun processIntent(intent: CountIntent) {
        when (intent) {
            is CountIntent.SetUnionId -> setUnionOfChipboardsAndRelatedChipboards(intent.unionId)
            is CountIntent.SizeChanged -> sortBySize(
                intent.newSizeAsString,
                intent.dimension
            )

            is CountIntent.QuantityChanged -> sortByQuantity(intent.newQuantityAsString)
            is CountIntent.ColorChanged -> sortByColor(intent.colorName, intent.color)

            is CountIntent.DifferenceChanged -> updateDifferenceForSize(
                intent.newDiffAsString,
                intent.dimension
            )


            CountIntent.SetFoundChipboard -> setFound()
            CountIntent.CreateUnknownChipboard -> createUnknownAndSaveInDb()
            is CountIntent.PressOnItemInList -> pressOnItemInList(intent.chipboard)
            is CountIntent.ShowWhatIs -> viewModelScope.launch {
                _effect.send(CountEffect.ShowWhatIsDialog(intent.questionType))
            }

//            is CountIntent.AskUncheckChipboard -> viewModelScope.launch {
//                _effect.send(CountEffect.ShowUncheckConfirmationDialog(intent.chipboard))
//            }

            is CountIntent.UncheckChipboardConfirmed -> setChipboardAsNotFound(intent.chipboard)
//            is CountIntent.AskSelectToFindArea -> viewModelScope.launch {
//                _effect.send(CountEffect.ShowSelectToFindAreaConfirmationDialog(intent.chipboard))
//            }

            is CountIntent.SelectNotFoundToFindAreaConfirmed -> setChipboardInFindArea(intent.chipboard)
            is CountIntent.RemoveNotFoundFromFindAreaConfirmed -> removeNotFoundChipboardFromFindArea(intent.chipboard)
            is CountIntent.ToggleFindAreaVisibility -> {
                _state.update { it.copy(isFoundAreaOpen = !it.isFoundAreaOpen) }
            }

            is CountIntent.SetListDone -> setListDoneOrUnDone()

        }
    }


    private fun setUnionOfChipboardsAndRelatedChipboards(unionId: Int?) {
        viewModelScope.launch {
            if (unionId == null) {
                val unionOfChip = chipboardRepository.getLastUnFinishedUnionOfChipboards()
                    ?.toUnionOfChipboardsUI()
                getChipboards(unionOfChip)
            } else {
                val unionOfChip =
                    chipboardRepository.getUnionOfChipboardsById(unionId)?.toUnionOfChipboardsUI()
                getChipboards(unionOfChip)
            }
        }
    }


    private suspend fun getChipboards(unionOfChip: UnionOfChipboardsUI?) {
        if (unionOfChip == null) {
            _state.update { it.copy(isNoLists = true) }
            return
        }
        _state.update {
            it.copy(
                unionOfChipboards = unionOfChip,
                isNoLists = false
            )
        }
        chipboardRepository.getChipboardsByUnionIdFlow(unionOfChip.id)
            .collect { chipboards ->
                val updatedChipboards = chipboards.sortedWith(
                    compareBy {
                        if (it.state == 0) 0 else if (it.state == 2) 1 else 2
                    }
                ).map {
                    it.toChipboardUi().copy(
                        quantityAsString = it.quantity.toString(),
                        size1AsString = it.size1.toString(),
                        size2AsString = it.size2.toString(),
                        size3AsString = it.size3.toString(),
                        chipboardAsString = getChipboardAsString(it.toChipboardUi())
                    )
                }

                val initialChipboard =
                    getChipboardWithInitialValuesAndCharacteristics(updatedChipboards.firstOrNull())
                _state.update {
                    it.copy(
                        isNoLists = updatedChipboards.isEmpty(),
                        chipboards = updatedChipboards,
                        chipboardToFind = initialChipboard

                    )
                }
            }
    }


    private fun updateDifferenceForSize(newDiffAsString: String, dimension: Int) {
        val newDiffAsFloat = newDiffAsString.toFloatOrNull() ?: 0f

        _state.update { currentState ->
            val currentChipboard = currentState.chipboardToFind
            val updatedChipboard = when (dimension) {
                1 -> currentChipboard.copy(
                    diff1AsString = newDiffAsString,
                    difference1 = newDiffAsFloat
                )

                2 -> currentChipboard.copy(
                    diff2AsString = newDiffAsString,
                    difference2 = newDiffAsFloat
                )

                3 -> currentChipboard.copy(
                    diff3AsString = newDiffAsString,
                    difference3 = newDiffAsFloat
                )

                else -> currentChipboard
            }
            val allDiffsAsString = getAllDiffsAsString(updatedChipboard)
            currentState.copy(chipboardToFind = updatedChipboard.copy(allDiffsAsString = allDiffsAsString))
        }
    }


    private fun setChipboardInFindArea(chipboard: ChipboardUi) {
        //logic for chipboard with state = 0 - not found
        //set in the chipboard isUnderReview = true (also in the list)
        //set chipboard in state.chipboardToFind
        //set state.isFoundButtonAvailable = true
        //set state.isFoundAreaOpen = true
        //FlashFindItemArea as _effect.send(AddNewItemEffect.FlashFindItemArea)

        //logic for chipboard with state = 2 - unknown
        //set chipboard isUnderReview = false in the list for all chipboards
        //set chipboard in state.chipboardToFind
        //delete chipboard from db
        //set state.isFoundButtonAvailable = false, and state.isUnknownButtonAvailable = true
        //set state.isFoundAreaOpen = true
        //FlashFindItemArea as _effect.send(AddNewItemEffect.FlashFindItemArea)

        viewModelScope.launch {
            _state.update { currentState ->
                val updatedChipboards = currentState.chipboards.map {
                    if (it.isUnderReview) {
                        it.copy(isUnderReview = false)
                    } else{
                        if (it.id == chipboard.id) {
                            if (chipboard.state == 0) {
                                chipboard.copy(isUnderReview = true)
                            } else {
                                it
                            }
                        } else {
                            it
                        }
                    }

                }

                val isFoundButtonAvailable = chipboard.state == 0
                val isUnknownButtonAvailable = chipboard.state == 2

                currentState.copy(
                    chipboards = updatedChipboards,
                    chipboardToFind = chipboard.copy(isUnderReview = chipboard.state == 0),
                    isFoundButtonAvailable = isFoundButtonAvailable,
                    isUnknownButtonAvailable = isUnknownButtonAvailable,
                    isFoundAreaOpen = true
                )
            }

            if (chipboard.state == 2) {
                chipboardRepository.deleteChipboardById(chipboard.id)
            }

            _effect.send(CountEffect.FlashFindItemArea)
        }
    }


    private fun removeNotFoundChipboardFromFindArea(chipboard: ChipboardUi) {
        //find chipboard in the list and set chipboard.isUnderReview = false
        //set chipboard default values in state.chipboardToFind
        //set state.isFoundButtonAvailable = false
        //set state.isUnknownButtonAvailable = false

        _state.update { currentState ->
            val updatedChipboards = currentState.chipboards.map {
                if (it.id == chipboard.id) {
                    it.copy(isUnderReview = false)
                } else {
                    it
                }
            }

            val defaultChipboardToFind = getChipboardWithInitialValuesAndCharacteristics(chipboard)

            currentState.copy(
                chipboards = updatedChipboards,
                chipboardToFind = defaultChipboardToFind,
                isFoundButtonAvailable = false,
                isUnknownButtonAvailable = false
            )
        }
    }


    private fun setChipboardAsNotFound(chipboard: ChipboardUi) {
        //find chipboard in the list and set chipboard.state = 0
        //update state.chipboards

        _state.update { currentState ->
            val updatedChipboards = currentState.chipboards.map {
                if (it.id == chipboard.id) {
                    it.copy(state = 0)
                } else {
                    it
                }
            }
            currentState.copy(chipboards = updatedChipboards)
        }
    }


    private fun pressOnItemInList(chipboard: ChipboardUi) {
        when (chipboard.state) {
            0 -> { // Not found
                viewModelScope.launch {
                    if (chipboard.isUnderReview) {
                        _effect.send(CountEffect.ShowRemoveNotFoundFromFindAreaConfirmationDialog(chipboard))
                    } else {
                        _effect.send(CountEffect.ShowSelectNotFoundToFindAreaConfirmationDialog(chipboard))
                    }
                }
            }

            1 -> { // Found
                viewModelScope.launch {
                    _effect.send(CountEffect.ShowUncheckConfirmationDialog(chipboard))
                }
            }

            2 -> { // Unknown
                viewModelScope.launch {
                    _effect.send(CountEffect.ShowSelectUnknownToFindAreaConfirmationDialog(chipboard))
                }
            }

            else -> {}
        }
    }


    private fun setFound() {
        viewModelScope.launch {
            _state.value.chipboardToFind.let { chipboardToFind ->
                chipboardRepository.updateChipboardState(chipboardToFind.id, 1)

                _state.update { currentState ->
                    val currentChipboard = currentState.chipboardToFind
                    currentState.copy(
                        chipboardToFind = getChipboardWithInitialValuesAndCharacteristics(
                            currentChipboard
                        ),
                        isFoundButtonAvailable = false,
                        isUnknownButtonAvailable = false
                    )

                }
            }
        }
    }


    private fun createUnknownAndSaveInDb() {
        //set chipboardToFind.state = 2
        //save chipboardToFind in db
        //set characteristics to default values

        viewModelScope.launch {
            _state.value.chipboardToFind.let { chipboardToFind ->
                val unknownChipboard = chipboardToFind.copy(state = 2)
                chipboardRepository.insertChipboard(unknownChipboard.toChipboard())

                _state.update { currentState ->
                    val currentChipboard = currentState.chipboardToFind

                    currentState.copy(
                        chipboardToFind = getChipboardWithInitialValuesAndCharacteristics(
                            currentChipboard
                        ),
                        isUnknownButtonAvailable = false
                    )
                }
            }
        }
    }


    private fun sortBySize(findSizeAsString: String, dimension: Int) {
        //sort not found chipboard by this side:
        //find a size by dimension (dimension = 1 -> size1, dimension = 2 -> size2, dimension = 3 -> size3)
        //then sort only those with state = 0
        //and sorting logic is: for example findSizeAsFloat = 1.0
        //a) first find a size with full findSizeAsFloat - "1.0" and place them on top (if more than one chipboard is found)
        //b) then take a findSizeAsFloat without last number and find a size which starts with "1." and place them below found in a) section
        //c) then reduce a findSizeAsFloat one more - so result is "1" and find a size which starts with "1"  and place them below found in b) section
        //general logic is - first find exact size and place on top, then reduce a findSizeAsFloat on one figure from the end and find a size which starts with this string
        //and do the same until findSizeAsFloat ends with figures
        //for example, if findSizeAsFloat is 123.45 - so, chipboards have to be aligned next order:
        //a)on top those which size is exactly 123.45
        //b)then those which size start with "123.4"
        //c)then those which size start with "123."
        //d)then those which size start with "123"
        //e)then those which size start with "12"
        //f)then those which size start with "1"
        //g)then rest of chipboards
        //The logic has to be applied only to not found chipboards (state = 0) and only to chipboards with same dimension

        _state.update { currentState ->
            val chipboardsToSort = currentState.chipboards.filter { it.state == 0 }
            val otherChipboards = currentState.chipboards.filter { it.state != 0 }

            val sortedChipboards = chipboardsToSort.sortedWith(
                compareBy { chipboard ->
                    val chipboardSizeString = when (dimension) {
                        1 -> chipboard.size1AsString
                        2 -> chipboard.size2AsString
                        3 -> chipboard.size3AsString
                        else -> ""
                    }

                    // Generate a list of matching prefixes for the current chipboard size
                    val matchingPrefixes = mutableListOf<String>()
                    var currentPrefix = findSizeAsString
                    while (currentPrefix.isNotEmpty()) {
                        matchingPrefixes.add(currentPrefix)
                        currentPrefix = currentPrefix.dropLast(1)
                    }

                    // Find the index of the best match in the matchingPrefixes list
                    val bestMatchIndex = matchingPrefixes.indexOfFirst { prefix ->
                        chipboardSizeString.startsWith(prefix)
                    }

                    // If no match is found, assign a high index to place it at the end
                    if (bestMatchIndex == -1) Int.MAX_VALUE else bestMatchIndex
                }
            )

            val finalSortedList = sortedChipboards + otherChipboards
            currentState.copy(chipboards = finalSortedList)
        }
    }


    private fun sortByQuantity(findQuantityAsString: String) {
        //sort not found chipboard by quantity among those only with state = 0
        //and sorting logic is: for example findQuantityAsString = "2"
        //a) first find a quantity with  "2" and place them on top (if more than one chipboard is found)
        //b)then rest of chipboards
        //The logic has to be applied only to not found chipboards (state = 0)

        _state.update { currentState ->
            val chipboardsToSort = currentState.chipboards.filter { it.state == 0 }
            val otherChipboards = currentState.chipboards.filter { it.state != 0 }

            val sortedChipboards = chipboardsToSort.sortedWith(
                compareBy { chipboard ->
                    if (chipboard.quantityAsString == findQuantityAsString) 0 else 1
                }
            )

            val finalSortedList = sortedChipboards + otherChipboards

            currentState.copy(chipboards = finalSortedList)
        }
    }


    private fun sortByColor(colorName: String, color: Int) {
        //sort not found chipboard by colorName among those only with state = 0
        //and sorting logic is: for example colorName = "White"
        //a) first find a colorName with  "White" and place them on top (if more than one chipboard is found)
        //b)then rest of chipboards
        //The logic has to be applied only to not found chipboards (state = 0)
        _state.update { currentState ->
            val chipboardsToSort = currentState.chipboards.filter { it.state == 0 }
            val otherChipboards = currentState.chipboards.filter { it.state != 0 }

            val sortedChipboards = chipboardsToSort.sortedWith(
                compareBy { chipboard ->
                    if (chipboard.colorName == colorName) 0 else 1
                }
            )

            val finalSortedList = sortedChipboards + otherChipboards
            currentState.copy(chipboards = finalSortedList)
        }
    }


    private fun setListDoneOrUnDone() {
        //set unionOfChipboards.isFinished to opposite of current value
        //set updatedAt = System.currentTimeMillis()
        //save unionOfChipboards in db
        //update state with new unionOfChipboards

        viewModelScope.launch {
            _state.value.unionOfChipboards.let { currentUnion ->

                val updatedUnion = currentUnion.copy(
                    isFinished = !currentUnion.isFinished,
                    updatedAt = System.currentTimeMillis()
                )

                chipboardRepository.setUnionOfChipboardsIsFinished(
                    currentUnion.id,
                    !currentUnion.isFinished,
                    currentUnion.updatedAt
                )

                _state.update { currentState ->
                    currentState.copy(
                        unionOfChipboards = updatedUnion
                    )
                }
            }
        }
    }


    private fun setUnknownButtonAvailability(chipboard: ChipboardUi): Boolean {
        var isUnknownButtonAvailable = true
        for (i in 1..chipboard.dimensions) {
            when (i) {
                1 -> {
                    if (chipboard.size1 == 0f) isUnknownButtonAvailable = false
                }

                2 -> {
                    if (chipboard.size2 == 0f) isUnknownButtonAvailable = false
                }

                3 -> {
                    if (chipboard.size3 == 0f) isUnknownButtonAvailable = false
                }
            }
            if (chipboard.quantity.toInt() == 0) isUnknownButtonAvailable = false
        }
        return isUnknownButtonAvailable
    }


    private fun getChipboardWithInitialValuesAndCharacteristics(chipboard: ChipboardUi?): ChipboardUi {
        if (chipboard == null) return ChipboardUi()
        //set chipboardToFind characteristics taken from chipboard:
        //unionId, dimensions, direction, colorName, color, title1, title2, title3 - all characteristics that all chipboards share(except colorName, color)
        //but: id = 0, state = 0, quantity = 1, size1 = 0f, difference1 = 0f, size2 = 0f, difference2 = 0f, size3 = 0f, difference3 = 0f - all characteristics that different for most of time for chipboards
        //also after setting characteristics - calculate and set: quantityAsString (from quantity), size1AsString (from size1), size2AsString (from size2), size3AsString (from size3)
        //diff1AsString (from difference1), diff2AsString (from difference2), diff3AsString (from difference3)
        //chipboardAsString from fun getChipboardAsString
        //update chipboardToFind in state

        val newChipboardToFind = chipboard.copy(
            id = 0,
            state = 0,
            quantity = 1,
            size1 = 0f,
            difference1 = 0f,
            size2 = 0f,
            difference2 = 0f,
            size3 = 0f,
            difference3 = 0f,

            quantityAsString = "1",
            size1AsString = "",
            size2AsString = "",
            size3AsString = "",
            diff1AsString = "",
            diff2AsString = "",
            diff3AsString = "",
            chipboardAsString = ""
        )

        return newChipboardToFind.copy(
            chipboardAsString = getChipboardAsString(newChipboardToFind)
        )
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


    private fun getAllDiffsAsString(chipboard: ChipboardUi): String {
        //↑12.5 x 54.0 - 3 - chipboard as String
        // 12.7   53.8    - diffs as string
        //        53.8   - diffs as string
        // 12.7          - diffs as string
        val builder = StringBuilder()
        var isAllDiffsEmpty = true

        for (i in 1..chipboard.dimensions) {
            if (chipboard.direction.toInt() == i) {
                builder.append(" ")
            }

            val sizeString = when (i) {
                1 -> chipboard.size1.toString()
                2 -> chipboard.size2.toString()
                3 -> chipboard.size3.toString()
                else -> ""
            }

            val difference = when (i) {
                1 -> chipboard.difference1
                2 -> chipboard.difference2
                3 -> chipboard.difference3
                else -> 0f
            }

            if (difference != 0f) {
                isAllDiffsEmpty = false
                val diffString = difference.toString()
                builder.append(diffString)
                if (sizeString.length > diffString.length) {
                    builder.append(" ".repeat(sizeString.length - diffString.length))
                }
            } else {
                builder.append(" ".repeat(sizeString.length))
            }

            if (i < chipboard.dimensions) {
                builder.append("   ")
            }
        }

        return if (isAllDiffsEmpty) {
            ""
        } else {
            builder.toString()
        }
    }

}