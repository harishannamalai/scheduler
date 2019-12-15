package com.optile.recritment.scheduler.actions;

import com.optile.recritment.scheduler.error.InvalidActionException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * This application context aware factory will look up the definition of the action mapping and
 * will provide the necessary processor. The processor will implement the specific type of action.
 */

@Component
public class ActionFactory implements ApplicationContextAware {

    @Value("#{${action.types.map}}")
    private Map<String, String> actionMapping;

    ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * Look up the mapping and determine the processor bean, get processor bean from context and
     * return the processor.
     *
     * @param action
     * @return
     */

    public Action getAction(String action) {

        String processorBean = actionMapping.get(action);
        if (processorBean == null || processorBean.isEmpty()) {
            throw new InvalidActionException("Invalid action type \"" + action + "\"");
        }
        try {
            Action processor = (Action) this.context.getBean(processorBean);
            Optional.of(processor)
                    .orElseThrow(() -> new InvalidActionException("No Bean for Action type " + action + " found!"));

            return processor;
        } catch (Exception e) {
            throw new InvalidActionException("Error while trying to get action: " + action, e);
        }
    }
}
