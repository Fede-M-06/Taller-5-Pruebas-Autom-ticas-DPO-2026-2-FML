package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.HamburguesaException;
import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest {
	private static final String CARPETA_FACTURAS = "./facturas/";
	private static final String INGREDIENTES_OK = "lechuga;1000\ntomate;1000\n\nqueso;2500\n";
	private static final String MENU_OK = "corral;14000\npapas medianas;5500\n\ngaseosa;5000\n";
	private static final String COMBOS_OK = "combo test;10%;corral;papas medianas;gaseosa\n\ncombo doble;20%;corral;corral\ncombo medio;9.5%;corral;gaseosa\n";
	private Restaurante restaurante;

	@BeforeEach
	public void setUp() {
		restaurante = new Restaurante();
		// Calude AI ayudo con esto y con los siguientes metodos
		new File(CARPETA_FACTURAS).mkdirs();
	}

	private File crearArchivo(String nombre, String contenido) throws IOException {
		File archivo = File.createTempFile(nombre, ".txt");
		archivo.deleteOnExit();
		FileWriter out = new FileWriter(archivo);
		out.write(contenido);
		out.close();
		return archivo;
	}

	private void cargar(String ingredientes, String menu, String combos) throws Exception {
		restaurante.cargarInformacionRestaurante(crearArchivo("ingredientes", ingredientes), crearArchivo("menu", menu),
				crearArchivo("combos", combos));
	}

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
	@DisplayName("Retautante nuevo vacio")
	public void testRestauranteNUevoEstaVacio() {
		assertNull(restaurante.getPedidoEnCurso(), "Un restaurante nuevo no tiene pedido en curso.");
		assertTrue(restaurante.getPedidos().isEmpty(), "Un restaurante nuevo no tiene pedidos.");
		assertTrue(restaurante.getIngredientes().isEmpty(), "Un restaurante nuevo no tiene ingredientes.");
		assertTrue(restaurante.getMenuBase().isEmpty(), "Un restaurante nuevo no tiene menu base.");
		assertTrue(restaurante.getMenuCombos().isEmpty(), "Un restaurante nuevo no tiene combos.");
	}

	@Test
	@DisplayName("prueba de cargar informacion")
	public void testCargarInformacion() throws Exception {
		cargar(INGREDIENTES_OK, MENU_OK, COMBOS_OK);

		ArrayList<Ingrediente> ingredientes = restaurante.getIngredientes();
		assertEquals(3, ingredientes.size(), "Se esperaban 3 ingredientes");
		assertEquals("lechuga", ingredientes.get(0).getNombre());
		assertEquals(1000, ingredientes.get(0).getCostoAdicional());
		assertEquals("queso", ingredientes.get(2).getNombre());
		assertEquals(2500, ingredientes.get(2).getCostoAdicional());

		ArrayList<ProductoMenu> menu = restaurante.getMenuBase();
		assertEquals(3, menu.size(), "Se esperaban 3 productos en el menu");
		assertEquals("corral", menu.get(0).getNombre());
		assertEquals(14000, menu.get(0).getPrecio());
		assertEquals("gaseosa", menu.get(2).getNombre());
		assertEquals(5000, menu.get(2).getPrecio());

		ArrayList<Combo> combos = restaurante.getMenuCombos();
		assertEquals(3, combos.size(), "Se esperaban 3 combos");
		assertEquals("combo test", combos.get(0).getNombre());
		assertEquals(22050, combos.get(0).getPrecio());
		assertEquals("combo doble", combos.get(1).getNombre());
		assertEquals(22400, combos.get(1).getPrecio());
		assertEquals("combo medio", combos.get(2).getNombre());
		assertEquals(17195, combos.get(2).getPrecio());

	}

	@Test
	@DisplayName("prueba de cargar informacion de los archivos txt")
	public void testCargarInformacionArchivosTXT() throws NumberFormatException, HamburguesaException, IOException {
		restaurante.cargarInformacionRestaurante(new File("./data/ingredientes.txt"), new File("./data/menu.txt"),
				new File("./data/combos.txt"));
		assertEquals(15, restaurante.getIngredientes().size());
		assertEquals(22, restaurante.getMenuBase().size());
		assertEquals(4, restaurante.getMenuCombos().size());
		assertEquals("combo especial", restaurante.getMenuCombos().get(3).getNombre());
		assertEquals(31222, restaurante.getMenuCombos().get(3).getPrecio());
	}

	@Test
	public void testCargarIngredientesRepetidos() {
		assertThrows(IngredienteRepetidoException.class,
				() -> cargar("lechuga;1000\ntomate;1000\nlechuga;2000\n", MENU_OK, COMBOS_OK));
	}

	@Test
	public void testCargarMenuRepetido() {
		assertThrows(ProductoRepetidoException.class,
				() -> cargar(INGREDIENTES_OK, "corral;14000\ngaseosa;5000\ncorral;15000\n", COMBOS_OK));
	}

	@Test
	public void testCargarCombosRepetido() {
		assertThrows(ProductoRepetidoException.class,
				() -> cargar(INGREDIENTES_OK, MENU_OK, "combo test;10%;corral;gaseosa\ncombo test;20%;corral\n"));
	}

	@Test
	public void testCargarComboConProductoFaltante() {
		assertThrows(ProductoFaltanteException.class,
				() -> cargar(INGREDIENTES_OK, MENU_OK, "combo raro;10%;corral;producto que no existe\n"));
	}

	@Test
	public void testIngredientesFormatoInvalido() {
		assertThrows(NumberFormatException.class, () -> cargar("lechuga;1000\ntomate;mil\n", MENU_OK, COMBOS_OK));
	}

	@Test
	public void testMenuFormatoInvalido() {
		assertThrows(NumberFormatException.class, () -> cargar(INGREDIENTES_OK, "corral;caro\n", COMBOS_OK));
	}

	@Test
	public void testCargarCombosFormatoInvalido() {
		assertThrows(NumberFormatException.class, () -> cargar(INGREDIENTES_OK, MENU_OK, "combo test;mucho%;corral\n"));
	}

	@Test
	public void testCargarArchivosInexistentes() throws Exception {
		File noExiste = new File("./no_existe.txt");
		File ingredientes = crearArchivo("ingredientes", INGREDIENTES_OK);
		File menu = crearArchivo("menu", MENU_OK);
		File combos = crearArchivo("combos", COMBOS_OK);
		assertThrows(IOException.class, () -> new Restaurante().cargarInformacionRestaurante(noExiste, menu, combos),
				"Ingredientes inexistente.");
		assertThrows(IOException.class,
				() -> new Restaurante().cargarInformacionRestaurante(ingredientes, noExiste, combos),
				"Menu inexistente.");
		assertThrows(IOException.class,
				() -> new Restaurante().cargarInformacionRestaurante(ingredientes, menu, noExiste),
				"Combos inexistente.");
	}

	@Test
	public void testIniciarPedido() throws Exception {
		restaurante.iniciarPedido("Ana", "Calle 1");

		Pedido pedido = restaurante.getPedidoEnCurso();
		assertNotNull(pedido, "debe haber un pedido en curso");
		assertEquals("Ana", pedido.getNombreCliente());
		assertTrue(restaurante.getPedidos().isEmpty(), "El pedido en curso aun no esta entre los pedidos cerrados.");

	}

	@Test
	public void testIniciarPedidoYaHayUnoEnCurso() throws Exception {
		restaurante.iniciarPedido("Ana", "Calle 1");
		YaHayUnPedidoEnCursoException e = assertThrows(YaHayUnPedidoEnCursoException.class,
				() -> restaurante.iniciarPedido("Luis", "Calle 2"));
		assertTrue(e.getMessage().contains("Ana"));
		assertTrue(e.getMessage().contains("Luis"));
		assertEquals("Ana", restaurante.getPedidoEnCurso().getNombreCliente(), "El pedido en curso no debe cambiar.");

	}

	@Test
	public void testCerrarYGuyardarSinPedido() {
		assertThrows(NoHayPedidoEnCursoException.class, () -> restaurante.cerrarYGuardarPedido());
	}

	@Test
	public void testCerrarYGuardarPedido() throws Exception {
		cargar(INGREDIENTES_OK, MENU_OK, COMBOS_OK);
		restaurante.iniciarPedido("Ana", "Calle 1");
		Pedido pedido = restaurante.getPedidoEnCurso();
		pedido.agregarProducto(restaurante.getMenuBase().get(0));
		pedido.agregarProducto(restaurante.getMenuCombos().get(0));
		String textoEsperado = pedido.generarTextoFactura();
		File factura = new File(CARPETA_FACTURAS + "factura_" + pedido.getIdPedido() + ".txt");
		restaurante.cerrarYGuardarPedido();
		assertNull(restaurante.getPedidoEnCurso(), "Ya no debe haber pedido en curso.");
		assertEquals(1, restaurante.getPedidos().size(), "El pedido cerrado debe quedar en el historico.");
		assertSame(pedido, restaurante.getPedidos().get(0));
		assertTrue(factura.exists(), "La factura debe guardarse en factura_<id>.txt dentro de la carpeta de facturas.");
		assertEquals(textoEsperado, leerArchivo(factura), "El archivo debe contener la factura del pedido.");
		factura.delete();
	}

	@Test
	public void testSePuedeIniciarOtroPedidoDespuesDeCerrar() throws Exception {
		restaurante.iniciarPedido("Ana", "Calle 1");
		File factura1 = new File(CARPETA_FACTURAS + "factura_" + restaurante.getPedidoEnCurso().getIdPedido() + ".txt");
		restaurante.cerrarYGuardarPedido();
		restaurante.iniciarPedido("Luis", "Calle 2");
		File factura2 = new File(CARPETA_FACTURAS + "factura_" + restaurante.getPedidoEnCurso().getIdPedido() + ".txt");
		restaurante.cerrarYGuardarPedido();
		assertEquals(2, restaurante.getPedidos().size());
		assertEquals("Ana", restaurante.getPedidos().get(0).getNombreCliente());
		assertEquals("Luis", restaurante.getPedidos().get(1).getNombreCliente());
		assertTrue(restaurante.getPedidos().get(0).getIdPedido() != restaurante.getPedidos().get(1).getIdPedido(),
				"Los Ids deben ser distintos");
		factura1.delete();
		factura2.delete();
	}

	@Test
	public void testIntegracionPedidoCompleto() throws Exception {
		cargar(INGREDIENTES_OK, MENU_OK, COMBOS_OK);
		restaurante.iniciarPedido("Ana", "Calle 1");
		Pedido pedido = restaurante.getPedidoEnCurso();
		ProductoMenu corral = restaurante.getMenuBase().get(0);
		Combo combo = restaurante.getMenuCombos().get(0);
		pedido.agregarProducto(corral);
		pedido.agregarProducto(combo);
		pedido.agregarProducto(new ProductoAjustado(restaurante.getMenuBase().get(2)));
		assertEquals(48849, pedido.getPrecioTotalPedido());
		File factura = new File(CARPETA_FACTURAS + "factura_" + pedido.getIdPedido() + ".txt");
		restaurante.cerrarYGuardarPedido();
		String contenido = leerArchivo(factura);
		assertTrue(contenido.contains("Cliente: Ana"));
		assertTrue(contenido.contains("Combo combo test"));
		assertTrue(contenido.contains("Precio Neto:  41050"));
		assertTrue(contenido.contains("IVA:          7799"));
		assertTrue(contenido.contains("Precio Total: 48849"));
		factura.delete();
	}

}
