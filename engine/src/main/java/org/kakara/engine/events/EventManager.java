package org.kakara.engine.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventManager {
    private List<Object> handlers;
    public EventManager(){
        handlers = new ArrayList();
    }

    /**
     * Register an event.
     * @param obj The object to register.
     */
    public void registerHandler(Object obj){
        handlers.add(obj);
    }

    /**
     * Fire an event.
     * @param eventInstance The instance of said event.
     */
    public void fireHandler(Object eventInstance){
        for(Object obj : handlers){
            List<Method> mtd = new ArrayList<Method>(Arrays.asList(obj.getClass().getDeclaredMethods()));
            for(Method msd : mtd){
                if(msd.getParameterCount() != 1) continue;
                if(msd.isAnnotationPresent(EventHandler.class)){
                    if(msd.getParameters()[0].getType() == eventInstance.getClass()){
                        try {
                            msd.invoke(obj, eventInstance);
                        }catch(IllegalAccessException | InvocationTargetException ex){
                            System.out.println("Error: Cannot fire event.");
                        }
                    }
                }
            }
        }
    }
}
