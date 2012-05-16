package ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph;


import static ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores.AMARILLO;
import static ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores.CELESTE;
import static ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores.NARANJA;
import static ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores.ROJO;
import static ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores.VERDE;
import static ar.gov.anses.prissa.asistente.graficador.modeloresumido.jgraph.Colores.VIOLETA;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class LeyendaComponent extends JPanel {

	public LeyendaComponent() {
		BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
		this.setLayout(bl);
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(getTabla1());
		this.add(getTabla2());
	}

	@SuppressWarnings("serial")
	private JTable getTabla1() {

		JTable table = new JTable(new Object[][] { { "Leyenda" } },
				new Object[] { "Leyenda" }) {

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
		return table;
	}

	private JTable getTabla2() {
		String[] columnNames = { "Color", "Condición" };
		Object[][] data = { { ROJO.color, ROJO.descripcion },
				{ NARANJA.color, NARANJA.descripcion },
				{ AMARILLO.color, AMARILLO.descripcion },
				{ VERDE.color, VERDE.descripcion },
				{ VIOLETA.color, VIOLETA.descripcion },
				{ CELESTE.color, CELESTE.descripcion } };
		final ColorRenderer cr = new ColorRenderer();
		JTable table = new JTable(data, columnNames) {
			@Override
			public Class<?> getColumnClass(int c) {
				return getValueAt(0, c).getClass();

			}
		};

		table.setDefaultRenderer(String.class, new LineWrapCellRenderer());

		table.setDefaultRenderer(Color.class, cr);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(550);

		table.setRowHeight(table.getRowHeight() * 2);

		table.setEnabled(false);
		table.setOpaque(true);

		return table;

	}

}

class LineWrapCellRenderer extends JTextArea implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		this.setText((String) value);
		this.setWrapStyleWord(true);
		this.setLineWrap(true);

//		int fontHeight = this.getFontMetrics(this.getFont()).getHeight();
//		int textLength = this.getText().length();
//		int lines = textLength ;// +1, cause we need at
//														// least 1 row.
//		int height = fontHeight * lines;
//		table.setRowHeight(row, height);

		return this;
	}

}
