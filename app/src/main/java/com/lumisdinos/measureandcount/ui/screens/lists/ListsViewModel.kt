package com.lumisdinos.measureandcount.ui.screens.lists

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumisdinos.measureandcount.data.MeasureAndCountRepository
import com.lumisdinos.measureandcount.data.db.model.UnionOfChipboards
import com.lumisdinos.measureandcount.ui.model.toUnionOfChipboardsUI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.comparisons.thenByDescending

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val chipboardRepository: MeasureAndCountRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(ListsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ListsEffects>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            chipboardRepository.getAllUnionsFlow()
                .collect { unions ->
                    val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)

                    val (unionsToDelete, unionsToKeep) = unions.partition { union ->
                        union.isMarkedAsDeleted && maxOf(union.createdAt, union.updatedAt) < thirtyDaysAgo
                    }

                    unionsToDelete.forEach { union ->
                        chipboardRepository.deleteUnionOfChipboards(union.id)
                        chipboardRepository.deleteAllChipboardsByUnionId(union.id)
                    }

                    val sortedUnions = unionsToKeep.sortedWith(
                        compareBy<UnionOfChipboards> {
                            if (it.isMarkedAsDeleted) 2 else {
                                if (it.isFinished) 1 else 0
                            }
                        }.thenByDescending { maxOf(it.createdAt, it.updatedAt) }
                    ).map {
                        it.toUnionOfChipboardsUI()
                    }
                    _state.update { it.copy(listOfUnions = sortedUnions) }
                }

        }
    }

    fun processIntent(intent: ListsIntent) {
        when (intent) {
            is ListsIntent.PressOnItemInList -> {
                viewModelScope.launch {
                    _effect.send(ListsEffects.NavigateToCountScreen(intent.union.id))
                }
            }
        }

    }

}