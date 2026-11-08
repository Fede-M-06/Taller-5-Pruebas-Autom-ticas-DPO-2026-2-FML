package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest {
	private Pedido pedido;
	private ProductoMenu corral;
	private ProductoMenu papas;
	private ProductoMenu gaseosa;

	@BeforeEach
	public void setUp() {
		pedido = new Pedido("Ana", "Calle 1 # 2-3");
		corral = new ProductoMenu("corral", 14000);
		papas = new ProductoMenu("papas medianas", 5500);
		gaseosa = new ProductoMenu("gaseosa", 5000);
	}

	// Claude Ai ayudo con el siguiente metodo
	private String leerArchivo(File archivo) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(archivo));
		StringBuffer sb = new StringBuffer();
		int c = reader.read();
		while (c != -1) {
			sb.append((char) c);
			c = reader.read();
		}
		reader.close();
		return sb.toString();
	}

	@Test
	@DisplayName("prueba de GetNombreCliente")
	public void testGetNombreCliente() {
		assertEquals("Ana", pedido.getNombreCliente(), "El nombre del pedido no es el esperado");
	}

	@Test
	@DisplayName("prueba de GetId pedido con sucesion")
	public void testIdConsecutivos() {
		Pedido segundo = new Pedido("Luis", "Carrera 5");
		Pedido tercero = new Pedido("Luisa", "Carrera 8");
		assertEquals(pedido.getIdPedido() + 1, segundo.getIdPedido(),
				"El id de cada pedido debe ser el siguiente al anterior.");
		assertEquals(segundo.getIdPedido() + 1, tercero.getIdPedido(),
				"El id de cada pedido debe ser el siguiente al anterior.");
	}

	@Test
	@DisplayName("prueba de un pedido vacio")
	public void testPrecioPedidoVacio() {
		assertEquals(0, pedido.getPrecioTotalPedido(), "Un pedido sin productos cuesta cero.");
	}

	@Test
	@DisplayName("prueba de el precio de un pedido con productos")
	public void testPRecioTotalConProductos() {
		pedido.agregarProducto(corral);
		pedido.agregarProducto(papas);
		// total 19500 e IVA 3705
		assertEquals(23205, pedido.getPrecioTotalPedido(), "El precio total debe ser el neto mas el IVA del 19%.");
	}

	@Test
	@DisplayName("prueba de pedido con diferentes tipos de producto")
	public void testPRecioTotalConProductosDistintos() {
		ArrayList<ProductoMenu> items = new ArrayList<ProductoMenu>();
		items.add(corral);
		items.add(papas);
		items.add(gaseosa);
		Combo combo = new Combo("combo corral", 0.1, items);
		pedido.agregarProducto(combo);
		pedido.agregarProducto(new ProductoAjustado(gaseosa));
		assertEquals(27050 + 5139, pedido.getPrecioTotalPedido());
	}

	@Test
	@DisplayName("factura pedido vacio")
	public void testGenerarTextoFacturaPedidoVacio() {
		String esperado = "Cliente: Ana\n" + "Dirección: Calle 1 # 2-3\n" + "----------------\n" + "----------------\n"
				+ "Precio Neto:  0\n" + "IVA:          0\n" + "Precio Total: 0\n";
		assertEquals(esperado, pedido.generarTextoFactura(), "La factura de un pedio vacio no es la esperada");
	}

	@Test
	@DisplayName("generar factura de un pedido completo")
	public void testGenerarTextoFactura() {
		pedido.agregarProducto(corral);
		pedido.agregarProducto(papas);
		String esperado = "Cliente: Ana\n" + "Dirección: Calle 1 # 2-3\n" + "----------------\n" + "corral\n"
				+ "            14000\n" + "papas medianas\n" + "            5500\n" + "----------------\n"
				+ "Precio Neto:  19500\n" + "IVA:          3705\n" + "Precio Total: 23205\n";
		assertEquals(esperado, pedido.generarTextoFactura(), "La factura del pedido no es la esperada.");
	}

	@Test
	@DisplayName("prueba de guardar facturas de un pedido vacio")
	public void testGuardarFacturasPedidoVacio() throws IOException {
		// Claude ai ayudo a usar File
		File archivo = File.createTempFile("factura_vacia", ".txt");
		archivo.deleteOnExit();
		pedido.guardarFactura(archivo);
		assertEquals(pedido.generarTextoFactura(), leerArchivo(archivo));
	}

	@Test
	@DisplayName("prueba de guardar facturas")
	public void testGuardarFactura() throws IOException {
		pedido.agregarProducto(corral);
		pedido.agregarProducto(gaseosa);
		File archivo = File.createTempFile("factura_prueba", ".txt");
		archivo.deleteOnExit();
		pedido.guardarFactura(archivo);
		assertTrue(archivo.exists(), "El archivo de la factura debe haberse creado.");
		assertEquals(pedido.generarTextoFactura(), leerArchivo(archivo));

	}

	@Test
	@DisplayName("prueba de guardar facturas en una carpeta no existente")
	public void testGuardarFacturasEnCarpetaExistente() {
		File archivo = new File("./carpeta_que_no_existe/factura.txt");
		assertThrows(FileNotFoundException.class, () -> pedido.guardarFactura(archivo),
				"Debe lanzar FileNotFoundException si no se puede crear el archivo.");
	}

}
