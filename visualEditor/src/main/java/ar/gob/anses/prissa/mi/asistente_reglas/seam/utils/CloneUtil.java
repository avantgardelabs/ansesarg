package ar.gob.anses.prissa.mi.asistente_reglas.seam.utils;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import ar.gob.anses.prissa.mi.asistente_reglas.entity.modelosemantico.IEntity;

public class CloneUtil {

	public static Object cloneEntity(Object o) {
		
			try {
				Object cloned = BeanUtils.cloneBean(o);
				return cloned;
			} catch (Exception e) {
				throw new RuntimeException(
						"No se puede clonar el objeto de tipo "
								+ o.getClass().getName(), e);
			}
		
	}
	
	public static List cloneList(List list) {
		List ret = new ArrayList(list.size());
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Object iEntity = iterator.next();
			ret.add(iEntity);
		}
		
		return ret;
	}

	/**
	 * Returns a deeply cloned java bean.
	 * 
	 * @param fromBean
	 *            java bean to be cloned.
	 * @return a new java bean cloned from fromBean.
	 */
	public static Object copyEntity(Object fromBean) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLEncoder out = new XMLEncoder(bos);
		out.writeObject(fromBean);
		out.close();
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		XMLDecoder in = new XMLDecoder(bis);
		Object toBean = in.readObject();
		in.close();
		return toBean;
	}

}
