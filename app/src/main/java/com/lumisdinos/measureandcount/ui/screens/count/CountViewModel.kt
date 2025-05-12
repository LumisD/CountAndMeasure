package com.lumisdinos.measureandcount.ui.screens.count

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.model.toUnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ConfirmationType
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

    private var isInitialChipboardSetForCurrentUnion: Boolean = false


    fun processIntent(intent: CountIntent) {
        when (intent) {
            is CountIntent.SetUnionId -> setUnionOfChipboardsAndRelatedChipboards(intent.unionId)
            is CountIntent.SizeChanged -> {
                sortBySize(intent.newSizeAsString, intent.dimension)
                updateChipboardSize(intent.newSizeAsString, intent.dimension)
            }

            is CountIntent.QuantityChanged -> handleChangedQuantity(intent.newQuantityAsString)

            is CountIntent.ColorChanged -> {
                sortByColor(intent.colorName, intent.color)
                updateChipboardColor(intent.colorName, intent.color)
            }

            is CountIntent.RealSizeChanged -> updateRealSizeForSize(
                intent.newDiffAsString,
                intent.dimension
            )

            CountIntent.SetFoundChipboard -> setFound()
            CountIntent.CreateUnknownChipboard -> createUnknownAndSaveInDb()
            is CountIntent.PressOnItemInList -> pressOnItemInList(intent.chipboard)
            is CountIntent.ShowWhatIs -> viewModelScope.launch {
                _effect.send(CountEffect.ShowWhatIsDialog(intent.questionType))
            }

            is CountIntent.ActionConfirmed -> {
                when (val confirmationType = intent.confirmationType) {
                    is ConfirmationType.UncheckChipboardConfirmed -> {
                        setChipboardAsNotFound(confirmationType.chipboard)
                    }

                    is ConfirmationType.SelectNotFoundToFindAreaConfirmed -> {
                        setChipboardInFindArea(confirmationType.chipboard)
                    }

                    is ConfirmationType.RemoveNotFoundFromFindAreaConfirmed -> {
                        removeNotFoundChipboardFromFindArea(confirmationType.chipboard)
                    }

                    is ConfirmationType.SelectUnknownToFindAreaConfirmed -> setChipboardInFindArea(
                        confirmationType.chipboard
                    )
                }
            }

            is CountIntent.ToggleFindAreaVisibility -> {
                _state.update { it.copy(isFoundAreaOpen = !it.isFoundAreaOpen) }
            }

            is CountIntent.SetListDone -> setListDoneOrUnDone()

            CountIntent.FieldDisabled -> viewModelScope.launch {
                _effect.send(CountEffect.ShowFieldDisabled)
            }
        }
    }


    private fun setUnionOfChipboardsAndRelatedChipboards(unionId: Int?) {
        isInitialChipboardSetForCurrentUnion = false
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
                        real1AsString = if (it.realSize1 != 0f) it.realSize1.toString() else "",
                        real2AsString = if (it.realSize2 != 0f) it.realSize2.toString() else "",
                        real3AsString = if (it.realSize3 != 0f) it.realSize3.toString() else "",
                        chipboardAsString = getChipboardAsString(it.toChipboardUi()),
                        allRealsAsString = getAllRealsAsString(it.toChipboardUi())
                    )
                }


                _state.update {
                    val updatedState = it.copy(
                        isNoLists = updatedChipboards.isEmpty(),
                        chipboards = updatedChipboards,
                    )
                    if (!isInitialChipboardSetForCurrentUnion) {
                        val initialChipboard =
                            getChipboardWithInitialValuesAndCharacteristics(updatedChipboards.firstOrNull())
                        isInitialChipboardSetForCurrentUnion = true
                        updatedState.copy(chipboardToFind = initialChipboard)
                    } else {
                        updatedState
                    }
                }
            }

    }


    private fun updateRealSizeForSize(newRealSizeAsString: String, dimension: Int) {
        val newRealSizeAsFloat = newRealSizeAsString.toFloatOrNull() ?: 0f

        viewModelScope.launch {
            _state.update { currentState ->
                val currentChipboard = currentState.chipboardToFind
                if (currentState.chipboardToFind.state != 0) return@update currentState
                val updatedChipboard = when (dimension) {
                    1 -> currentChipboard.copy(
                        real1AsString = newRealSizeAsString,
                        realSize1 = newRealSizeAsFloat
                    )

                    2 -> currentChipboard.copy(
                        real2AsString = newRealSizeAsString,
                        realSize2 = newRealSizeAsFloat
                    )

                    3 -> currentChipboard.copy(
                        real3AsString = newRealSizeAsString,
                        realSize3 = newRealSizeAsFloat
                    )

                    else -> currentChipboard
                }
                val allRealsAsString = getAllRealsAsString(updatedChipboard)
                val updatedChipboard2 = updatedChipboard.copy(allRealsAsString = allRealsAsString)
                currentState.copy(chipboardToFind = updatedChipboard2)
            }

        }
    }


    private fun setChipboardInFindArea(chipboard: ChipboardUi) {
        //logic for chipboard with state = 0 - not found
        //set in the chipboard isUnderReview = true (also in the list), isFoundButtonAvailable = true
        //set chipboard in state.chipboardToFind
        //set state.isFoundAreaOpen = true
        //FlashFindItemArea as _effect.send(AddNewItemEffect.FlashFindItemArea)

        //logic for chipboard with state = 2 - unknown
        //set chipboard isUnderReview = false in the list for all chipboards, isFoundButtonAvailable = false
        //set chipboard in state.chipboardToFind
        //delete chipboard from db
        // state.isUnknownButtonAvailable = true
        //set state.isFoundAreaOpen = true
        //FlashFindItemArea as _effect.send(AddNewItemEffect.FlashFindItemArea)

        viewModelScope.launch {
            _state.update { currentState ->
                val updatedChipboards = currentState.chipboards.map {
                    if (it.isUnderReview) {
                        it.copy(isUnderReview = false)
                    } else {
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

                val chipboardsWithUnderReviewOnTop =
                    setItemWhichUnderReviewOnTopOfList(updatedChipboards)

                currentState.copy(
                    chipboards = chipboardsWithUnderReviewOnTop,
                    chipboardToFind = chipboard.copy(isUnderReview = chipboard.state == 0),
                    isUnknownButtonAvailable = chipboard.state == 2,
                    isFoundButtonAvailable = chipboard.state == 0,
                    isFoundAreaOpen = true
                )
            }

            if (chipboard.state == 2) {
                chipboardRepository.deleteChipboardById(chipboard.id)
            }

            _effect.send(CountEffect.FlashFindItemArea)
        }
    }


    private fun setItemWhichUnderReviewOnTopOfList(chipboards: List<ChipboardUi>): List<ChipboardUi> {
        val underReviewItem = chipboards.find { it.isUnderReview }
        return if (underReviewItem != null) {
            val mutableList = chipboards.toMutableList()
            mutableList.remove(underReviewItem)
            mutableList.add(0, underReviewItem)
            mutableList.toList()
        } else {
            chipboards
        }
    }


    private fun removeNotFoundChipboardFromFindArea(chipboard: ChipboardUi) {
        //find chipboard in the list and set chipboard.isUnderReview = false, isFoundButtonAvailable = false
        //set chipboard default values in state.chipboardToFind
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
                isUnknownButtonAvailable = false,
                isFoundButtonAvailable = false
            )
        }
    }


    private fun setChipboardAsNotFound(chipboard: ChipboardUi) {
        //find chipboard in the list and set chipboard.state = 0
        //update state.chipboards and real sizes
        //update chipboard in db

        viewModelScope.launch {
            _state.update { currentState ->
                val updatedChipboards = currentState.chipboards.map {
                    if (it.id == chipboard.id) {
                        it.copy(
                            state = 0,
                            realSize1 = 0f,
                            realSize2 = 0f,
                            realSize3 = 0f,
                            real1AsString = "",
                            real2AsString = "",
                            real3AsString = ""
                        )
                    } else {
                        it
                    }
                }
                currentState.copy(chipboards = updatedChipboards)
            }

            chipboardRepository.insertChipboard(
                chipboard.copy(
                    state = 0,
                    realSize1 = 0f,
                    realSize2 = 0f,
                    realSize3 = 0f,
                    real1AsString = "",
                    real2AsString = "",
                    real3AsString = ""
                ).toChipboard()
            )
        }
    }


    private fun pressOnItemInList(chipboard: ChipboardUi) {
        when (chipboard.state) {
            0 -> { // Not found
                viewModelScope.launch {
                    if (chipboard.isUnderReview) {
                        _effect.send(
                            CountEffect.ShowRemoveNotFoundFromFindAreaConfirmationDialog(
                                chipboard
                            )
                        )
                    } else {
                        _effect.send(
                            CountEffect.ShowSelectNotFoundToFindAreaConfirmationDialog(
                                chipboard
                            )
                        )
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
        //Starting points:
        //this function can be called only for chipboards with state = 0
        //qty can be equal or smaller than original qty
        //chipboardToFind can differ only with real sizes and qty from the chipboard in db with the same id

        //save chipboardToFind in db:
        //-check if qty of chipboardToFind corresponds the same qty of the chipboard with the same id in db
        //   -if yes (qty == qty in db) - find in db a chipboard/s (with the same unionId), not the same id, which has the same fields as chipboardToFind (except qty)
        //                                          and also is found (state = 1)
        //      -if yes(is found similar chipboard) - increase a quantity of that chipboard by quantity of chipboardToFind, then delete chipboardToFind (by id) from db
        //      -if no(is not found similar chipboard) - insert chipboardToFind in db as found (state = 1)
        //   -if no (qty != qty in db) Options:
        //       qty of chipboardToFind > qty in db - impossible according current logic, but anyway - show a dialog with a message, abort set found
        //       qty of chipboardToFind < qty in db
        //          two option: (depending on existing of the chipboard with the same fields as chipboardToFind (except qty))
        //            - no similar chipboard: create new chipboard with qty of chipboardToFind AND decrease qty of original chipboard with the same id in db
        //            - similar chipboard: increase a qty of that chipboard by qty of chipboardToFind, then delete chipboardToFind (by id) from db

        viewModelScope.launch {
            val chipboardToFind = _state.value.chipboardToFind

            val originalChipboardInDb = chipboardRepository.getChipboardByIdAndUnionId(
                chipboardToFind.id,
                chipboardToFind.unionId,
            )?.toChipboardUi()

            var isShoudSkipLogic = false

            if (originalChipboardInDb == null) {
                // This should not happen if the logic is correct,
                // but save in db chipboardToFind with current id
                val foundChipboard = chipboardToFind.copy(state = 1).toChipboard()
                chipboardRepository.insertChipboard(foundChipboard)
                isShoudSkipLogic = true
            }

            if (!isShoudSkipLogic) {
                val quantityOriginalInDb = originalChipboardInDb!!.quantity
                val quantityFromToFind = chipboardToFind.quantity
                //first to check: unionId, id != chipboardToFind.id, state = 1
                //second to check: all fields except qty - dimensions, direction, color, colorName
                //title1, size1, realSize1, title2, size2, realSize2, title3, size3, realSize3
                val similarFoundChipboard =
                    chipboardRepository.findSimilarFoundChipboard(chipboardToFind.toChipboard())
                        ?.toChipboardUi()

                when {
                    quantityFromToFind > quantityOriginalInDb -> {//impossible according current logic
                        Log.d("CountViewModel", "setFound quantityFromToFind > quantityOriginalInDbl -> impossible")
                        _effect.send(
                            CountEffect.ShowNotExceedingTargetQuantityDialog(
                                quantityOriginalInDb,
                                quantityFromToFind
                            )
                        )
                        return@launch
                    }

                    quantityFromToFind == quantityOriginalInDb -> {
                        if (similarFoundChipboard != null) {
                            // Similar found chipboard exist:
                            chipboardRepository.updateChipboardQuantity(//increase its quantity
                                similarFoundChipboard.id,
                                similarFoundChipboard.quantity + quantityFromToFind
                            )
                            chipboardRepository.deleteChipboardById(originalChipboardInDb.id)//delete original chipboard
                        } else {
                            // No similar found chipboard: insert chipboardToFind in db as found (state = 1)
                            chipboardRepository.insertChipboard(
                                chipboardToFind.copy(state = 1).toChipboard()
                            )
                        }
                    }

                    // qty of chipboardToFind < qty in db
                    quantityFromToFind < quantityOriginalInDb -> {
                        if (similarFoundChipboard != null) {
                            // Similar found chipboard exists:
                            chipboardRepository.updateChipboardQuantity(//increase its quantity
                                similarFoundChipboard.id,
                                similarFoundChipboard.quantity + quantityFromToFind
                            )
                            chipboardRepository.updateChipboardQuantity(//decrease qty of original chipboard in db
                                originalChipboardInDb.id,
                                originalChipboardInDb.quantity - quantityFromToFind
                            )
                        } else {
                            // No similar found chipboard: create new chipboard with qty of chipboardToFind
                            // AND decrease qty of original chipboard with the same id in db
                            val newFoundChipboard = chipboardToFind.copy(
                                id = 0,//to make db create new chipboard
                                state = 1
                            ).toChipboard()
                            chipboardRepository.insertChipboard(newFoundChipboard)
                            chipboardRepository.updateChipboardQuantity(originalChipboardInDb.id, originalChipboardInDb.quantity - quantityFromToFind)
                        }
                    }
                }

            }

            //finally:
            //set chipboardToFind to initial values and characteristics,
            //also isUnderReview = false (in getChipboardWithInitialValuesAndCharacteristics) in the list, also isFoundButtonAvailable = false

            _state.update { currentState ->
                val currentChipboard = currentState.chipboardToFind
                val updatedChipboards = currentState.chipboards.map {
                    if (it.isUnderReview) {
                        it.copy(isUnderReview = false)
                    } else {
                        it
                    }
                }
                currentState.copy(
                    chipboardToFind = getChipboardWithInitialValuesAndCharacteristics(
                        currentChipboard
                    ),
                    chipboards = updatedChipboards,
                    isUnknownButtonAvailable = false,
                    isFoundButtonAvailable = false
                )
            }

            _effect.send(CountEffect.FlashFindItemArea)
        }
    }


    private fun createUnknownAndSaveInDb() {
        //This function can be called only for chipboards with state = 2

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


    private fun handleChangedQuantity(newQuantityAsString: String) {
        //chipboardToFind can have only state 0 or 2 (not found and unknown)
        //if state == 0 - quantity cannot be bigger than the qty of the chipboard with the same id in the list
        //  so, if that happened - set chipboardToFind.quantity = chipboard.quantity AND show a dialog with a message about not exceeding target quantity
        //if state == 2 - quantity can be any number
        //don't sortByQuantity if a chipbord isUnderReview = true
        val newQuantityAsInt = newQuantityAsString.toIntOrNull() ?: 0
        val chipboardInFindArea = _state.value.chipboardToFind
        val isUnderReview = chipboardInFindArea.isUnderReview

        if (newQuantityAsInt in setOf<Int>(0, 1) || chipboardInFindArea.state == 2
        ) {//if qty is small OR it's unknown chipboard
            if (!isUnderReview) sortByQuantity(newQuantityAsString)
            updateChipboardQuantity(newQuantityAsString)
            return
        }

        val chipboardId = chipboardInFindArea.id
        val unionId = chipboardInFindArea.unionId
        viewModelScope.launch {
            val originalQuantity =
                chipboardRepository.getQuantityOfChipboardByConditions(chipboardId, unionId, 0)
            if (originalQuantity == -1 || newQuantityAsInt <= originalQuantity) {
                //if qty is not found in db(weird) OR new qty smaller than original qty
                if (!isUnderReview) sortByQuantity(newQuantityAsString)
                updateChipboardQuantity(newQuantityAsString)
            } else {
                //if new qty is bigger than original qty
                if (!isUnderReview) sortByQuantity(originalQuantity.toString())
                updateChipboardQuantity(originalQuantity.toString())
                _effect.send(
                    CountEffect.ShowNotExceedingTargetQuantityDialog(
                        originalQuantity,
                        newQuantityAsInt
                    )
                )
            }
        }
    }


    private fun sortByQuantity(findQuantityAsString: String) {
        //sort not found chipboard by quantity among those only with state = 0
        //and sorting logic is: for example findQuantityAsString = "2"
        //a) first find a quantity with  "2" and place them on top (if more than one chipboard is found)
        //b)then rest of chipboards
        //The logic has to be applied only to not found chipboards (state = 0)
        if (findQuantityAsString.isEmpty() || findQuantityAsString == "0") return

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


    private fun updateChipboardSize(newSizeAsString: String, dimension: Int) {
        val newSizeAsFloat = newSizeAsString.toFloatOrNull() ?: 0f

        _state.update { currentState ->
            if (currentState.chipboardToFind.state != 2) return@update currentState

            val currentChipboard = currentState.chipboardToFind
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
            val setUnknownButnAvailbl = setUnknownButtonAvailability(updatedChipboard)
            currentState.copy(
                chipboardToFind = updatedChipboard2,
                isUnknownButtonAvailable = setUnknownButnAvailbl
            )
        }
    }


    private fun updateChipboardColor(newColorName: String, newColor: Int) {
        _state.update { currentState ->
            if (currentState.chipboardToFind.state != 2) return@update currentState
            val updatedChipboard = currentState.chipboardToFind.copy(
                colorName = newColorName,
                color = newColor
            )
            val updatedChipboard2 =
                updatedChipboard.copy(chipboardAsString = getChipboardAsString(updatedChipboard))
            currentState.copy(
                chipboardToFind = updatedChipboard2,
            )
        }
    }

    private fun updateChipboardQuantity(newQuantityAsString: String) {
        val newQuantityAsInt = newQuantityAsString.toIntOrNull() ?: 0
        _state.update { currentState ->
            //if (currentState.chipboardToFind.state != 2) return@update currentState
            val updatedChipboard = currentState.chipboardToFind.copy(
                quantityAsString = newQuantityAsString,
                quantity = newQuantityAsInt
            )
            val updatedChipboard2 =
                updatedChipboard.copy(chipboardAsString = getChipboardAsString(updatedChipboard))
            val setUnknownButnAvailbl = setUnknownButtonAvailability(updatedChipboard)
            currentState.copy(
                chipboardToFind = updatedChipboard2,
                isUnknownButtonAvailable = setUnknownButnAvailbl,
                isFoundButtonAvailable = newQuantityAsInt > 0
            )
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
            state = 2,
            quantity = 1,
            size1 = 0f,
            realSize1 = 0f,
            size2 = 0f,
            realSize2 = 0f,
            size3 = 0f,
            realSize3 = 0f,

            quantityAsString = "1",
            size1AsString = "",
            size2AsString = "",
            size3AsString = "",
            real1AsString = "",
            real2AsString = "",
            real3AsString = "",
            chipboardAsString = "",

            isUnderReview = false
        )

        return newChipboardToFind.copy(
            chipboardAsString = getChipboardAsString(newChipboardToFind)
        )
    }


    private fun getChipboardAsString(chipboard: ChipboardUi): String {
        //↑12.5 x 54.0 - 3
        val builder = StringBuilder()
        for (i in 1..chipboard.dimensions) {
            if (chipboard.direction == i) {
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


    private fun getAllRealsAsString(chipboard: ChipboardUi): String {
        //↑12.5 x 54.0 - 3 - chipboard as String
        // 12.7   53.8    - diffs as string
        //        53.8   - diffs as string
        // 12.7          - diffs as string
        val builder = StringBuilder()
        var isAllRealsEmpty = true

        for (i in 1..chipboard.dimensions) {
            if (chipboard.direction == i) {
                builder.append(" ")
            }

            val sizeString = when (i) {
                1 -> chipboard.size1.toString()
                2 -> chipboard.size2.toString()
                3 -> chipboard.size3.toString()
                else -> ""
            }

            val realSize = when (i) {
                1 -> chipboard.realSize1
                2 -> chipboard.realSize2
                3 -> chipboard.realSize3
                else -> 0f
            }

            if (realSize != 0f) {
                isAllRealsEmpty = false
                val realSizeString = realSize.toString()
                builder.append(realSizeString)
                if (sizeString.length > realSizeString.length) {
                    builder.append(" ".repeat(sizeString.length - realSizeString.length))
                }
            } else {
                builder.append(" ".repeat(sizeString.length))
            }

            if (i < chipboard.dimensions) {
                builder.append("    ")
            }
        }

        return if (isAllRealsEmpty) {
            ""
        } else {
            builder.toString()
        }
    }


    private fun setUnknownButtonAvailability(chipboard: ChipboardUi): Boolean {
        if (chipboard.state != 2) return false
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
            if (chipboard.quantity == 0) isUnknownButtonAvailable = false
        }
        return isUnknownButtonAvailable
    }


}