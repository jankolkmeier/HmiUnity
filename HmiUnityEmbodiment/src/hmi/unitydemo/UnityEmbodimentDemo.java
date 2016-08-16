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

import asap.environment.AsapEnvironment;
import hmi.environmentbase.ClockDrivenCopyEnvironment;

import hmi.audioenvironment.AudioEnvironment;
import hmi.environmentbase.Environment;
import hmi.faceanimation.converters.FACS2MorphConverter;
import hmi.jcomponentenvironment.JComponentEnvironment;
import hmi.mixedanimationenvironment.MixedAnimationEnvironment;
import hmi.physicsenvironment.OdePhysicsEnvironment;
import hmi.unityembodiments.UnityEmbodiment;
import hmi.worldobjectenvironment.WorldObjectEnvironment;
import lombok.extern.slf4j.Slf4j;;

@Slf4j
public class UnityEmbodimentDemo {
    

    protected static JFrame mainJFrame = new JFrame("AsapRealizer demo");
    
    public UnityEmbodimentDemo() {
    }
    
    public static void main(String[] args) throws IOException {
    	MixedAnimationEnvironment mae = new MixedAnimationEnvironment();
        final OdePhysicsEnvironment ope = new OdePhysicsEnvironment();
        WorldObjectEnvironment we = new WorldObjectEnvironment();
        AudioEnvironment aue = new AudioEnvironment("LJWGL_JOAL");

        ArrayList<Environment> environments = new ArrayList<Environment>();
        final JComponentEnvironment jce = setupJComponentEnvironment();
        ClockDrivenCopyEnvironment ce = new ClockDrivenCopyEnvironment(1000 / 30);

        ce.init();
        ope.init();
        mae.init(ope, 0.002f);
        we.init();
        aue.init();
        environments.add(ope);
        environments.add(mae);
        environments.add(we);

        environments.add(ce);
        environments.add(jce);
        environments.add(aue);
        
        final AsapEnvironment ee = new AsapEnvironment();
        environments.add(ee);
        ee.init(environments, ope.getPhysicsClock());
        ope.addPrePhysicsCopyListener(ee);
        
        String spec = "unity_agentspec.xml";
        ee.loadVirtualHuman("", spec, "AsapRealizer demo");
        //AsapVirtualHuman avh = new AsapVirtualHuman();


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