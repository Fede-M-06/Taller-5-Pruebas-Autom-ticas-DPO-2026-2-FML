package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoMenuTest {
	private ProductoMenu producto;

	@BeforeEach
	public void setUp() {
		producto = new ProductoMenu("corral", 14000);
	}

	@Test
	@DisplayName("Prueba de GetNombre")
	public void tetsGetNombre() {
		assertEquals("corral", producto.getNombre(), "El nombre del producto no es el esperado");
	}

	@Test
	@DisplayName("Prueba de GetPrecio")
	public void tetsGetPrecio() {
		assertEquals(14000, producto.getPrecio(), "El precio del producto no es el esperado");
	}

	@Test
	@DisplayName("Prueba de generarTextoFactura")
	public void tetsGenerarTextoFactura() {
		String esperado = "corral\n            14000\n";
		assertEquals(esperado, producto.generarTextoFactura(), "El texto de la factura no es el esperado");
	}

	@Test
	@DisplayName("Prueba de con otro producto")
	public void tetsOtroProducto() {
		ProductoMenu otro = new ProductoMenu("gaseosa", 5000);
		assertEquals("gaseosa", otro.getNombre());
		assertEquals(5000, otro.getPrecio());
		String esperado = "gaseosa\n            5000\n";
		assertEquals(esperado, otro.generarTextoFactura());
	}

}
