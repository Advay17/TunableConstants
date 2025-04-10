package com.roboloco.tune;
import java.util.EnumSet;

import edu.wpi.first.networktables.MultiSubscriber;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableListenerPoller;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class TunableSubsystem extends SubsystemBase{
    public final TunableConstants[] linkedTunableConstants;
    private final NetworkTableListenerPoller poller;
    private final MultiSubscriber multiSubscriber;
    public TunableSubsystem(TunableConstants... linkedTunableConstants) {
        this(NetworkTableInstance.getDefault(), linkedTunableConstants);
    }
    public TunableSubsystem(NetworkTableInstance nTableInstance, TunableConstants... linkedTunableConstants) {
        this.linkedTunableConstants = linkedTunableConstants;
        this.poller = new NetworkTableListenerPoller(nTableInstance);
        String[] tableNames = new String[linkedTunableConstants.length];
        for(int i=0; i<linkedTunableConstants.length; i++){
            tableNames[i]="/Preferences/" + linkedTunableConstants.getClass().getSimpleName().substring(7) + "/";
        }
        multiSubscriber = new MultiSubscriber(nTableInstance, tableNames);
        poller.addListener(multiSubscriber, EnumSet.of(NetworkTableEvent.Kind.kValueAll));
    }

    @Override
    public void periodic() {
        if(poller.readQueue().length>0){
            for(TunableConstants c: linkedTunableConstants){
                c.reload();
            }
            reload();
        }
    }
    public abstract void reload();
}
