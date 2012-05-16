package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;

import ar.gov.anses.prissa.asistente.graficador.modeloresumido.TablaMR;

public class TablaVertexRendererMR implements CellViewRenderer {

	private static final long serialVersionUID = 5383639336146693578L;

	private TablaMR myModel;
	private JTable mainTable;
	private JPanel mainPanel;

	public TablaVertexRendererMR(TablaMR myModel) {
		this.myModel = myModel;
		this.mainTable = new JTable();
		mainTable.setModel(new TablaToTableModelAdapterMR(this.myModel));
		this.init();
	}

	private void init() {
		mainTable.setDefaultRenderer(Color.class, new ColorRenderer());
		mainTable.setOpaque(true);
		mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		autoResize(mainTable);
		mainTable.setShowGrid(true);
		
		mainPanel = new JPanel();
		BoxLayout bl = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
		mainPanel.setLayout(bl);
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		mainPanel.add(getTabla1());
		mainPanel.add(getTabla2());
		mainPanel.add(mainTable.getTableHeader());
		mainPanel.add(mainTable);
	}

	public Component getRendererComponent(JGraph graph, CellView view,
			boolean sel, boolean focus, boolean preview) {
	
		return mainPanel;
	}

	@SuppressWarnings("serial")
	private JTable getTabla1() {
		JTable table = new JTable(new Object[][] { { myModel.getTitulo() } },
				new Object[] { myModel.getTitulo() }) {

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				DefaultTableCellRenderer dtc = (DefaultTableCellRenderer) super
						.getCellRenderer(row, column);
				dtc.setHorizontalAlignment(SwingConstants.CENTER);
				return dtc;
			}
		};

		table.setBackground(table.getTableHeader().getBackground());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setOpaque(true);
		return table;
	}

	@SuppressWarnings("serial")
	private JTable getTabla2() {

		JTable table = new JTable(1, 1) {
			@Override
			public Class<?> getColumnClass(int column) {
				return Date.class;
			}

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				if (column == 0) {
					DefaultTableCellRenderer dtc = (DefaultTableCellRenderer) super
							.getCellRenderer(row, column);
					dtc.setHorizontalAlignment(SwingConstants.RIGHT);
					return dtc;
				}
				else return super.getCellRenderer(row, column);
			}
		};

		//table.setDefaultRenderer(JLinkButton.class, new JLinkButton("Editar"));
		//try {
			//JLinkButton jlk = new JLinkButton(new URL(myModel.getUrlEdicion()));
			//table.setValueAt(jlk, 0, 0);
			table.setValueAt(myModel.getFecha(), 0, 0);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//		} catch (MalformedURLException e) {
//			throw new RuntimeException(e);
//		};
		mainTable.setOpaque(true);
		return table;

	}

	private void autoResize(JTable table) {

		//table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		int margin = 5;
		TableColumnModel columnModel = table.getColumnModel();

		int maxHeight = 0;

		for (int i = 0; i < table.getColumnCount(); i++) {

			int height = 0;

			int columnIndex = i;
			TableColumn column = columnModel.getColumn(columnIndex);
			int width = 0;

			/* obtiene el tamaño del header */
			TableCellRenderer renderer = column.getHeaderRenderer();

			if (renderer == null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}

			Component comp = renderer.getTableCellRendererComponent(table,
					column.getHeaderValue(), false, false, -1, 0);

			width = comp.getPreferredSize().width;
			height += comp.getPreferredSize().height;

			/* obtiene el tamaño de cada celda */
			for (int r = 0; r < table.getRowCount(); r++) {
				renderer = table.getCellRenderer(r, columnIndex);
				comp = renderer.getTableCellRendererComponent(table, table
						.getValueAt(r, columnIndex), false, false, r,
						columnIndex);
				width = Math.max(width, comp.getPreferredSize().width);

				height += comp.getPreferredSize().height;

				/* workaround para ajustar el alto */
				height--;
			}

			/* agrega margen */
			width += 2 * margin;

			/* asigna el ancho */
			column.setPreferredWidth(width);

			/* actualiza el alto */
			maxHeight = Math.max(height, maxHeight);
		}

		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);

		Dimension size = new Dimension(table.getPreferredSize().width,
				maxHeight);

		//table.setPreferredSize(size);
		table.setSize(size);
		table.setMaximumSize(size);
		table.setMinimumSize(size);
	}

	public double getOffsetColumna(int nroColumna) {

		TableColumnModel columnModel = mainTable.getColumnModel();

		int offset = 0;

		/* acumula la distancia desde el borde hasta la columna anterior */
		for (int i = 0; i < nroColumna; i++) {

			offset += columnModel.getColumn(i).getPreferredWidth();

		}

		/* luego hasta llegar hasta la mitad de la columna */
		offset += (columnModel.getColumn(nroColumna).getPreferredWidth() / 2);

		return offset;
	}

	
	public JPanel getMainPanel() {
		return mainPanel;
	}

}

class ColorRenderer extends JLabel implements TableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ColorRenderer() {
		this.setOpaque(true); // MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(JTable table, Object val,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Color newColor = (Color) val;
		this.setBackground(newColor);
		return this;
	}
}
