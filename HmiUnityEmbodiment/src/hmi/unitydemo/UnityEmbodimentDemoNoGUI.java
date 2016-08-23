package hmi.unitydemo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import asap.bml.ext.bmlt.BMLTInfo;
import asap.environment.AsapEnvironment;
import hmi.audioenvironment.AudioEnvironment;
import hmi.environmentbase.ClockDrivenCopyEnvironment;
import hmi.environmentbase.Environment;
import hmi.jcomponentenvironment.JComponentEnvironment;
import hmi.mixedanimationenvironment.MixedAnimationEnvironment;
import hmi.physicsenvironment.OdePhysicsEnvironment;
import hmi.worldobjectenvironment.WorldObjectEnvironment;
import lombok.extern.slf4j.Slf4j;
import saiba.bml.BMLInfo;
import saiba.bml.core.FaceLexemeBehaviour;
import saiba.bml.core.HeadBehaviour;
import saiba.bml.core.PostureShiftBehaviour;;

@Slf4j
public class UnityEmbodimentDemoNoGUI
{
    public UnityEmbodimentDemoNoGUI()
    {
    }

    public static void main(String[] args) throws IOException
    {
        MixedAnimationEnvironment mae = new MixedAnimationEnvironment();
        final OdePhysicsEnvironment ope = new OdePhysicsEnvironment();
        WorldObjectEnvironment we = new WorldObjectEnvironment();
        AudioEnvironment aue = new AudioEnvironment("LJWGL_JOAL");

        BMLTInfo.init();
        BMLInfo.addCustomFloatAttribute(FaceLexemeBehaviour.class, "http://asap-project.org/convanim", "repetition");
        BMLInfo.addCustomStringAttribute(HeadBehaviour.class, "http://asap-project.org/convanim", "spindirection");
        BMLInfo.addCustomFloatAttribute(PostureShiftBehaviour.class, "http://asap-project.org/convanim", "amount");

        ArrayList<Environment> environments = new ArrayList<Environment>();
        final AsapEnvironment ee = new AsapEnvironment();
        
        ClockDrivenCopyEnvironment ce = new ClockDrivenCopyEnvironment(1000 / 60);

        ce.init();
        ope.init();
        mae.init(ope, 0.002f);
        we.init();
        aue.init();
        environments.add(ee);
        environments.add(ope);
        environments.add(mae);
        environments.add(we);

        environments.add(ce);
        environments.add(aue);

        ee.init(environments, ope.getPhysicsClock());
        ope.addPrePhysicsCopyListener(ee);

        String spec = "unity_agentspec_nogui_uma.xml";
        ee.loadVirtualHuman("", spec, "AsapRealizer demo");

        ope.startPhysicsClock();
    }
}
