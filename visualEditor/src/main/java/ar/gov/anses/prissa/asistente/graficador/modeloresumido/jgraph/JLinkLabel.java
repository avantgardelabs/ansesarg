package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


/**
 * The utillib library.
 * More information is available at http://www.jinchess.com/.
 * Copyright (C) 2002 Alexander Maryanovsky.
 * All rights reserved.
 *
 * The utillib library is free software; you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * The utillib library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with utillib library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


/**
 * An extension of JLabel which looks like a link and responds appropriately
 * when clicked. Note that this class will only work with Swing 1.1.1 and later.
 * Note that because of the way this class is implemented, getText() will not
 * return correct values, user <code>getNormalText</code> instead.
 */

public class JLinkLabel extends JLabel{



  /**
   * The normal text set by the user.
   */

  private String text;




  /**
   * Creates a new LinkLabel with the given text.
   */

  public JLinkLabel(String text){
	  
	 
    super(text);

    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    enableEvents(MouseEvent.MOUSE_EVENT_MASK);
  }




  /**
   * Sets the text of the label.
   */

  public void setText(String text){
    super.setText("<html><font color=\"#0000CF\"><u>"+text+"</u></font></html>"); //$NON-NLS-1$ //$NON-NLS-2$
    this.text = text;
  }




  /**
   * Returns the text set by the user.
   */

  public String getNormalText(){
    return text;
  }




  /**
   * Processes mouse events and responds to clicks.
   */

  protected void processMouseEvent(MouseEvent evt){
    super.processMouseEvent(evt);
    if (evt.getID() == MouseEvent.MOUSE_CLICKED)
      fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getNormalText()));
  }




  /**
   * Adds an ActionListener to the list of listeners receiving notifications
   * when the label is clicked.
   */

  public void addActionListener(ActionListener listener){
    listenerList.add(ActionListener.class, listener);
  }




  /**
   * Removes the given ActionListener from the list of listeners receiving
   * notifications when the label is clicked.
   */

  public void removeActionListener(ActionListener listener){
    listenerList.remove(ActionListener.class, listener);
  }




  /**
   * Fires an ActionEvent to all interested listeners.
   */

  protected void fireActionPerformed(ActionEvent evt){
    Object [] listeners = listenerList.getListenerList();
    for (int i = 0; i < listeners.length; i += 2){
      if (listeners[i] == ActionListener.class){
        ActionListener listener = (ActionListener)listeners[i+1];
        listener.actionPerformed(evt);
      }
    }
  }




//public Component getTableCellRendererComponent(JTable table, Object value,
//		boolean isSelected, boolean hasFocus, int row, int column) {
//	// TODO Auto-generated method stub
//	try {
//		return new JLinkButton("Pepe", new URL("http://www.google.com"));
//	} catch (MalformedURLException e) {
//		throw new RuntimeException(e);
//	}
//}


}