package ar.gob.anses.prissa.mi.asistente_reglas.seam.action.cq;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

public class ConexionBaseCQ {

	private String conexion_url = "jdbc:jtds:sqlserver://ANSESQASQL:1433/AnsesCQUser2";
	private String conexion_user = "cqmaster";
	private String conexion_pass = "cqmaster";

	private Connection conn = null;

	@Logger
	Log log;

	public Connection dbConnect() {
		try {
			if (conn == null) {
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
				conn = DriverManager.getConnection(getConexion_url(), getConexion_user(), getConexion_pass());
				System.out.println("Conectado exitosamente a la BBDD de Clear Quest");
			} else {
				System.out.println("La conexion a la BBDD de CQ ya estaba establecida");
			}
			return conn;

		} catch (Exception e) {
			System.out.println("Error al conectarse a la BBDD Clear Quest");
			e.printStackTrace();
			return null;
		}
	}

	public String getConexion_url() {
		if (PropertiesHelper.getInstance().getProperties("cq") != null) {
			conexion_url = PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.url");
		}
		return conexion_url;
	}

	public void setConexion_url(String conexionUrl) {
		conexion_url = conexionUrl;
	}

	public String getConexion_user() {
		if (PropertiesHelper.getInstance().getProperties("cq") != null) {
			conexion_user = PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.user");
		}
		return conexion_user;
	}

	public void setConexion_user(String conexionUser) {
		conexion_user = conexionUser;
	}

	public String getConexion_pass() {
		if (PropertiesHelper.getInstance().getProperties("cq") != null) {
			conexion_pass = PropertiesHelper.getInstance().getProperties("cq").getProperty("conexion.pass");
		}
		return conexion_pass;
	}

	public void setConexion_pass(String conexionPass) {
		conexion_pass = conexionPass;
	}
};