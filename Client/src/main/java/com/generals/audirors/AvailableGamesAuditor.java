package com.generals.audirors;

import com.generals.serialized_models.AvailableGameInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AvailableGamesAuditor {
    private List<AvailableGameInfo> availableGames;
    private AtomicBoolean isAvailableGamesChanged;

    AvailableGamesAuditor(List<AvailableGameInfo> availableGames, AtomicBoolean isAvailableGamesChanged) {
        this.availableGames = availableGames;
        this.isAvailableGamesChanged = isAvailableGamesChanged;
    }


}
