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
import hmi.util.Console;
import hmi.worldobjectenvironment.WorldObjectEnvironment;
import saiba.bml.BMLInfo;
import saiba.bml.core.FaceLexemeBehaviour;
import saiba.bml.core.HeadBehaviour;
import saiba.bml.core.PostureShiftBehaviour;;

public class UnityEmbodimentDemo
{

    protected static JFrame mainJFrame = new JFrame("AsapRealizer demo");

    public UnityEmbodimentDemo()
    {
    }

    public static void main(String[] args) throws IOException
    {
    	Console.setEnabled(false);
    	System.out.println("Args:");
    	for (int a = 0; a<args.length; a++) {
    		System.out.println("\tArg "+a+": "+args[a]);
    	}

        String spec = "unity_agentspec_uma.xml";
    	if (args.length > 0) {
    		spec = args[0];
    	}
    	
    	System.out.println("Using Agentspec: "+spec);
    	
        MixedAnimationEnvironment mae = new MixedAnimationEnvironment();
        final OdePhysicsEnvironment ope = new OdePhysicsEnvironment();
        WorldObjectEnvironment we = new WorldObjectEnvironment();
        AudioEnvironment aue = new AudioEnvironment("LJWGL_JOAL");

        BMLTInfo.init();
        BMLInfo.addCustomFloatAttribute(FaceLexemeBehaviour.class, "http://asap-project.org/convanim", "repetition");
        BMLInfo.addCustomStringAttribute(HeadBehaviour.class, "http://asap-project.org/convanim", "spindirection");
        BMLInfo.addCustomFloatAttribute(PostureShiftBehaviour.class, "http://asap-project.org/convanim", "amount");

        ArrayList<Environment> environments = new ArrayList<Environment>();
        final JComponentEnvironment jce = setupJComponentEnvironment();
        final AsapEnvironment ee = new AsapEnvironment();
        
        ClockDrivenCopyEnvironment ce = new ClockDrivenCopyEnvironment(1000 / 60);
        //ClockDrivenCopyEnvironment ce = new ClockDrivenCopyEnvironment(1000 / 2);

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
        environments.add(jce);
        environments.add(aue);

        ee.init(environments, ope.getPhysicsClock());
        ope.addPrePhysicsCopyListener(ee);

        ee.loadVirtualHuman("", spec, "AsapRealizer demo");

        ope.startPhysicsClock();

        mainJFrame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(WindowEvent winEvt)
            {
                System.exit(0);
            }
        });

        mainJFrame.setSize(1000, 600);
        mainJFrame.setVisible(true);
    }

    private static JComponentEnvironment setupJComponentEnvironment()
    {
        final JComponentEnvironment jce = new JComponentEnvironment();
        try
        {
            SwingUtilities.invokeAndWait(() -> {
                mainJFrame.setLayout(new BorderLayout());

                JPanel jPanel = new JPanel();
                jPanel.setPreferredSize(new Dimension(400, 40));
                jPanel.setLayout(new GridLayout(1, 1));
                jce.registerComponent("textpanel", jPanel);
                mainJFrame.add(jPanel, BorderLayout.SOUTH);
            });
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
        return jce;
    }
}
