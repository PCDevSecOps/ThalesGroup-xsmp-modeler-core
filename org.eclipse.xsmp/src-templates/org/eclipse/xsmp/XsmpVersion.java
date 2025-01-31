/*******************************************************************************
* Copyright (C) 2020-2022 THALES ALENIA SPACE FRANCE.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License 2.0
* which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/
package org.eclipse.xsmp;

/**
 * Helper class to get the current version of XSMP
 */
public interface XsmpVersion
{
  /**
   * The current version of this plugin
   */
  String VERSION = "${qualifiedVersion}";
}
