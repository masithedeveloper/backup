package com.money.randing.ui.chart;

import java.lang.System;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/money/randing/ui/chart/ChartViewModel;", "Landroidx/lifecycle/ViewModel;", "movementRepository", "Lcom/money/randing/domain/repository/movement/MovementRepository;", "(Lcom/money/randing/domain/repository/movement/MovementRepository;)V", "movements", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/money/randing/domain/model/Movement;", "getMovements", "()Lkotlinx/coroutines/flow/Flow;", "app_debug"})
public final class ChartViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> movements = null;
    private final com.money.randing.domain.repository.movement.MovementRepository movementRepository = null;
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.money.randing.domain.model.Movement>> getMovements() {
        return null;
    }
    
    @javax.inject.Inject()
    public ChartViewModel(@org.jetbrains.annotations.NotNull()
    com.money.randing.domain.repository.movement.MovementRepository movementRepository) {
        super();
    }
}