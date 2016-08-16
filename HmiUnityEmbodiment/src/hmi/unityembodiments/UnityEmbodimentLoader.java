package hmi.unityembodiments;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import hmi.environmentbase.CopyEnvironment;
import hmi.environmentbase.Embodiment;
import hmi.environmentbase.EmbodimentLoader;
import hmi.environmentbase.Environment;
import hmi.environmentbase.Loader;
import hmi.xml.XMLScanException;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnityEmbodimentLoader implements EmbodimentLoader {

	UnityEmbodiment ue;

	@Override
	public String getId() {
		return ue.getId();
	}

	@Override
	public void readXML(XMLTokenizer tokenizer, String loaderId, String vhId, String vhName, Environment[] environments,
			Loader... requiredLoaders) throws IOException {
		
		CopyEnvironment ce = null;
       	for (Environment e : environments)
        {
            if (e instanceof CopyEnvironment) ce = (CopyEnvironment) e;
        }
        if (ce == null)
        {
            throw new RuntimeException("UnityMechanimEmbodiment requires an Environment of type CopyEnvironment");
        }
		
        if(!tokenizer.atSTag("MiddlewareOptions"))
        {
            throw new XMLScanException("UnityMechanimEmbodiment requires an inner MiddlewareOptions element");            
        }
        
        HashMap<String, String> attrMap = tokenizer.getAttributes();
        XMLStructureAdapter adapter = new XMLStructureAdapter();
        String loaderclass = adapter.getRequiredAttribute("loaderclass", attrMap, tokenizer);

        tokenizer.takeSTag("MiddlewareOptions");

        Properties props = new Properties();
        while (tokenizer.atSTag("MiddlewareProperty")) {
            HashMap<String, String> attrMap2 = tokenizer.getAttributes();
            XMLStructureAdapter adapter2 = new XMLStructureAdapter();
            props.put(adapter2.getRequiredAttribute("name", attrMap, tokenizer),adapter2.getRequiredAttribute("value", attrMap, tokenizer));
            tokenizer.takeSTag("MiddlewareProperty");
            tokenizer.takeETag("MiddlewareProperty"); 
        }
        
        ue = new UnityEmbodiment(vhId, loaderId, loaderclass, props);
    	ue.RequestAgent(vhId, "/scene");
    	
    	while (!ue.isConfigured()) {
    		try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				log.info("Interrupted waiting for skel def: ",e);
			}
    	}
		
        ce.addCopyEmbodiment(ue);
        tokenizer.takeETag("MiddlewareOptions"); 
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Embodiment getEmbodiment() {
		return ue;
	}

}
