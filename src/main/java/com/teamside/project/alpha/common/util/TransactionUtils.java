package com.teamside.project.alpha.common.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionUtils {
    @Transactional(propagation = Propagation.REQUIRED)
    public void runTransaction(Action action) {
        action.act();
    }
    public interface Action {
        void act();
    }
}
