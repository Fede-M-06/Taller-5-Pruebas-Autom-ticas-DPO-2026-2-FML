package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest {
	private ProductoMenu base;
	private ProductoAjustado sinAjustes;
	private ProductoAjustado conAjustes;
	private Ingrediente queso;
	private Ingrediente huevo;
	private Ingrediente tomate;

	@BeforeEach
	public void setUp() {
		base = new ProductoMenu("corral", 14000);
		queso = new Ingrediente("queso mozzarella", 2500);
		tomate = new Ingrediente("tomate", 1000);
		huevo = new Ingrediente("huevo", 2500);

		sinAjustes = new ProductoAjustado(base);
		conAjustes = new ProductoAjustado(base);
		conAjustes.agregarIngrediente(queso);
		conAjustes.agregarIngrediente(huevo);
		conAjustes.eliminarIngrediente(tomate);
	}

	@Test
	@DisplayName("prueba de GetNombre")
	public void testGetNombre() {
		assertEquals("corral", sinAjustes.getNombre(), "El nombre debe ser el del producto base.");
		assertEquals("corral", conAjustes.getNombre(),
				"El nombre debe ser el del producto base aun asi tenga ajustes.");
	}

	@Test
	@DisplayName("prueba de GetPrecio pero sin ajustes")
	public void testGePrecioSinAjsutes() {
		assertEquals(14000, sinAjustes.getPrecio(), "Sin ajustes el precio debe ser el del producto base.");
	}

	@Test
	@DisplayName("prueba de GetPrecio pero con ajustes")
	public void testGePrecioConAjsutes() {
		// 1400 + 2500+ 2500 = 19000, como el tomate esta en ingredientes eliminados no
		// efecta el precio con ajustes
		assertEquals(19000, conAjustes.getPrecio(), "El precio debe sumar los agregados y no restar los eliminados.");
	}

	@Test
	@DisplayName("prueba de que un mismo ingrediente agregado dos veces se cobra dos veces.")
	public void testGetPrecioIngredienteRepetido() {
		ProductoAjustado doble = new ProductoAjustado(base);
		doble.agregarIngrediente(queso);
		doble.agregarIngrediente(queso);
		assertEquals(19000, doble.getPrecio(), "Un mismo ingrediente agregado dos veces se cobra dos veces.");

	}

	@Test
	@DisplayName("prueba de que la factura sin ajustes es la factura de la base")
	public void testGenerarTextoFacturaSinAjustes() {
		String esperado = "corral\n            14000\n";
		assertEquals(esperado, sinAjustes.generarTextoFactura(), "La factura sin ajustes es la factura de la base");
	}

	@Test
	@DisplayName("prueba de que la factura con ajustes")
	public void testGenerarTextoFacturaConAjustes() {
		String esperado = "corral\n" + "    +queso mozzarella                2500\n"
				+ "    +huevo                2500\n" + "    -tomate\n" + "            19000\n";
		assertEquals(esperado, conAjustes.generarTextoFactura(),
				"La factura de un producto con ajustes no es la esperada.");
	}

}
