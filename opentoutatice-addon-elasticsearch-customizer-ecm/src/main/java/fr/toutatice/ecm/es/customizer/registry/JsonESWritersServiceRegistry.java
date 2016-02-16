/**
 * 
 */

package fr.toutatice.ecm.es.customizer.registry;

import java.util.LinkedList;
import java.util.List;

import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.toutatice.ecm.es.customizer.writers.ICustomJsonESWriter;


/**
 * @author david
 */
public class JsonESWritersServiceRegistry extends DefaultComponent {
	
	/**
	 * Custom ES writer point.
	 */
	private static String WRITERS_EXT_POINT = "writers";
	
	/**
	 * Custom writers.
	 */
	private List<ICustomJsonESWriter> writers;
	
	/**
	 * @return registered custom ES writers.
	 */
	public List<ICustomJsonESWriter> getCustomJsonESWriters(){
		return this.writers;
	}
	
	@Override
    public void activate(ComponentContext context) throws Exception {
        super.activate(context);
        writers = new LinkedList<ICustomJsonESWriter>();
    }
	
	@Override
	public void registerContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
			throws Exception {
		
		if(WRITERS_EXT_POINT.equals(extensionPoint)){
			JsonESWriterDescriptor desc = (JsonESWriterDescriptor) contribution;
			if(desc.isEnabled()){
				String className = desc.getClazz();
				ICustomJsonESWriter clazzInstance = (ICustomJsonESWriter) Class.forName(className).newInstance();
				int order = desc.getOrder();
				writers.add(order, clazzInstance);
			}
		}
		
	}

}
