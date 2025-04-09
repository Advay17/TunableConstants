package com.roboloco.buttonvisualizer;

import java.util.HashMap;
import java.util.function.BooleanSupplier;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class VisualizableTrigger implements BooleanSupplier{
    public static HashMap<String, String> bindingMap = new HashMap<String, String>();
    protected String bindString;
    private Trigger trigger;
    public VisualizableTrigger(EventLoop eventLoop, BooleanSupplier condition, String bindString) {
        trigger = new Trigger(eventLoop, condition);
        this.bindString = bindString;
    }
    public VisualizableTrigger(BooleanSupplier condition, String bindString) {
        this(CommandScheduler.getInstance().getDefaultButtonLoop(), condition, bindString);
    }
    public String getBindString(){
        return bindString;
    }
    public VisualizableTrigger onTrue(Command command, String name){
        trigger.onTrue(command);
        bindingMap.put(name, bindString + "OT");
        return this;
    }
    public VisualizableTrigger onFalse(Command command, String name){
        trigger.onFalse(command);
        bindingMap.put(name, bindString + "OF");
        return this;
    }
    public VisualizableTrigger whileTrue(Command command, String name){
        trigger.whileTrue(command);
        bindingMap.put(name, bindString + "WT");
        return this;
    }
    public VisualizableTrigger whileFalse(Command command, String name){
        trigger.whileFalse(command);
        bindingMap.put(name, bindString + "WF");
        return this;
    }
    public VisualizableTrigger onChange(Command command, String name){
        trigger.onChange(command);
        bindingMap.put(name, bindString + "OC");
        return this;
    }
    public VisualizableTrigger toggleOnTrue(Command command, String name){
        trigger.toggleOnTrue(command);
        bindingMap.put(name, bindString + "TOT");
        return this;
    }
    public VisualizableTrigger toggleOnFalse(Command command, String name){
        trigger.toggleOnFalse(command);
        bindingMap.put(name, bindString + "TOF");
        return this;
    }

    public VisualizableTrigger and(BooleanSupplier trigger){
        this.trigger = this.trigger.and(trigger);
        return this;
    }

    public VisualizableTrigger and(VisualizableTrigger trigger){
        this.trigger = this.trigger.and(trigger);
        bindString += "&" + trigger.getBindString();
        return this;
    }
    public VisualizableTrigger or(BooleanSupplier trigger){
        this.trigger = this.trigger.or(trigger);
        return this;
    }
    public VisualizableTrigger or(VisualizableTrigger trigger){
        this.trigger = this.trigger.or(trigger);
        bindString += "|" + trigger.getBindString();
        return this;
    }
    public VisualizableTrigger negate(){
        this.trigger = this.trigger.negate();
        bindString+="!";
        return this;
    }

    public VisualizableTrigger debounce(double seconds, DebounceType type){
        this.trigger = trigger.debounce(seconds, type);
        bindString+="DB";
        return this;
    }
    public VisualizableTrigger debounce(double seconds){
        return debounce(seconds, DebounceType.kRising);
    }

    @Override
    public boolean getAsBoolean() {
        return trigger.getAsBoolean();
    }

}
