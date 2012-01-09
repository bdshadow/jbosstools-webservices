/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Xavier Coulon - Initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder;

import static org.eclipse.jdt.core.IJavaElementDelta.CHANGED;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.IJavaElementDelta;
import org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain.JaxrsElement;
import org.jboss.tools.ws.jaxrs.core.internal.utils.ConstantUtils;
import org.jboss.tools.ws.jaxrs.core.internal.utils.Logger;
import org.jboss.tools.ws.jaxrs.core.metamodel.EnumKind;

public class JaxrsElementDelta implements Comparable<JaxrsElementDelta> {

	private final JaxrsElement<?> element;

	private final int deltaKind;

	private final int flags;

	/** No change. */
	public static final int F_NONE = 0;

	/** Meaning that the change occurred at a finer level. */
	public static final int F_FINE_GRAINED = 1;

	public static final int F_ELEMENT_KIND = 2;

	public static final int F_PATH_VALUE = 4;

	public static final int F_APPLICATION_PATH_VALUE = 8;

	public static final int F_HTTP_METHOD_VALUE = 16;

	public static final int F_PATH_PARAM_VALUE = 32;

	public static final int F_QUERY_PARAM_VALUE = 64;

	public static final int F_MATRIX_PARAM_VALUE = 128;

	public static final int F_DEFAULT_VALUE_VALUE = 256;

	public static final int F_CONSUMED_MEDIATYPES_VALUE = 512;

	public static final int F_PRODUCED_MEDIATYPES_VALUE = 1024;

	public static final int F_METHOD_PARAMETERS = 2048;

	public static final int F_METHOD_RETURN_TYPE = 4096;

	/**
	 * Full constructor.
	 * 
	 * @param element
	 * @param deltaKind
	 */
	public JaxrsElementDelta(JaxrsElement<?> element, int deltaKind) {
		this(element, deltaKind, 0);
	}

	/**
	 * Full constructor.
	 * 
	 * @param element
	 * @param deltaKind
	 * @param flags
	 */
	public JaxrsElementDelta(JaxrsElement<?> element, int deltaKind, int flags) {
		this.element = element;
		this.deltaKind = deltaKind;
		this.flags = flags;
		if (this.deltaKind == CHANGED && this.flags == F_NONE) {
			Logger.debug("*** No flag to describe the change ?!? ***");
		}
	}

	/** @return the element */
	public JaxrsElement<?> getElement() {
		return element;
	}

	/** @return the deltaKind */
	public int getDeltaKind() {
		return deltaKind;
	}

	/** @return the flags */
	public int getFlags() {
		return flags;
	}

	/**
	 * {@inheritDoc} (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("JaxrsElementChange: [").append(ConstantUtils.toCamelCase(element.getElementKind().toString()))
				.append(" ").append(ConstantUtils.getStaticFieldName(IJavaElementDelta.class, deltaKind)).append("] ")
				.append(element.getJavaElement().getElementName());

		try {
			if (flags != F_NONE) {
				List<String> matchFlags = new ArrayList<String>();
				for (Field field : JaxrsElementDelta.class.getFields()) {
					if ((flags & field.getInt(field)) > 0) {
						matchFlags.add(ConstantUtils.getStaticFieldName(JaxrsElementDelta.class,
								field.getInt(field), "F_"));
					}
				}
				s.append(":{");
				for (Iterator<String> iterator = matchFlags.iterator(); iterator.hasNext();) {
					String flag = iterator.next();
					s.append(flag);
					if (iterator.hasNext()) {
						s.append("+");
					}
				}
				s.append("}");
			}
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}

		return s.toString();
	}

	@Override
	public int compareTo(JaxrsElementDelta other) {
		final EnumKind elementKind = this.element.getKind();
		final EnumKind otherElementKind = other.getElement().getKind();
		return elementKind.ordinal() - otherElementKind.ordinal();
	}

}