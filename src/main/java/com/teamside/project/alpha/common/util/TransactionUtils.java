package com.teamside.project.alpha.common.util;

import com.teamside.project.alpha.common.exception.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionUtils {
    @Transactional(propagation = Propagation.REQUIRED)
    public void runTransaction(Action action) throws CustomException {
        action.act();
    }
    public interface Action {
        void act() throws CustomException;
    }
}
