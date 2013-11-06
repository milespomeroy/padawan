package com.milespomeroy.padawan;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.IntegerMapper;

public class HelloServlet extends HttpServlet {
	private DBI dbi;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try
		{
			Context context = new InitialContext();
			DataSource dataSource = (DataSource)context.lookup("java:comp/env/jdbc/pg");
			this.dbi = new DBI(dataSource);
		}
		catch(NamingException | ClassCastException e)
		{
			throw new ServletException("Failure getting JNDI reference for Postgres.", e);
		}

		this.dbi.registerArgumentFactory(new PostgresIntegerArrayArgumentFactory());

		try (Handle handle = this.dbi.open()) {
			handle.execute("DROP TABLE IF EXISTS tester");
			handle.execute("CREATE TABLE tester (id int, val int)");
			handle.execute("INSERT INTO tester VALUES (1, 5)");
			handle.execute("INSERT INTO tester VALUES (2, 5)");
			handle.execute("INSERT INTO tester VALUES (3, 5)");
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int sum;

		try (Handle handle = this.dbi.open()) {
			sum = handle
					.createQuery(
							"SELECT SUM(val) AS s FROM tester WHERE id = any(:ary)")
					.map(IntegerMapper.FIRST)
					.bind("ary", SqlArray.arrayOf(Integer.class, 1, 2)).first();
		}

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("<h1 style=\"\n" + 
				"    position: absolute;\n" + 
				"    left: 50%;\n" + 
				"    top: 50%;\n" + 
				"    height: 50px;\n" + 
				"    width: 50px;\n" + 
				"    text-align: center;\n" + 
				"    margin: -25px 0 0 -25px;\n" + 
				"    font: normal 40px monospace;\n" + 
				"    line-height: 50px;\n" + 
				"\">" + sum + "</h1>");
	}
}
