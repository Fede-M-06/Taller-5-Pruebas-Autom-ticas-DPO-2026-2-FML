package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest {
	private ProductoMenu corral;
	private ProductoMenu papas;
	private ProductoMenu gaseosa;
	private ArrayList<ProductoMenu> items;
	private Combo combo;

	@BeforeEach
	public void setUp() {
		corral = new ProductoMenu("corral", 14000);
		papas = new ProductoMenu("papas medianas", 5500);
		gaseosa = new ProductoMenu("gaseosa", 5000);
		items = new ArrayList<ProductoMenu>();
		items.add(gaseosa);
		items.add(corral);
		items.add(papas);

		combo = new Combo("combo corral", 0.1, items);
	}

	@Test
	@DisplayName("prueba de GetNombre")
	public void testGetNombre() {
		assertEquals("combo corral", combo.getNombre(), "El nombre del combo no es el esperado");
	}

	@Test
	@DisplayName("prueba de GetPrecio con 0.1 de descuento")
	public void testGetPrecioConDescuento() {
		assertEquals(22050, combo.getPrecio(), "El precio del combo debe ser el de los productos menos el descuento.");
	}

	@Test
	@DisplayName("prueba de GetPrecio con 0 de descuento")
	public void testGetPrecioSinDescuento() {
		Combo sinDescuento = new Combo("combo sin descuento", 0, items);
		assertEquals(24500, sinDescuento.getPrecio(),
				"El precio del combo con 0 de descuento debe ser la suma del precio de los items.");
	}

	@Test
	@DisplayName("prueba de GetPrecio con 1 de descuento")
	public void testGetPrecioConDescuentoTotal() {
		Combo gratis = new Combo("combo gratis", 1, items);
		assertEquals(0, gratis.getPrecio(),
				"El precio del combo con 0 de descuento debe ser la suma del precio de los items.");
	}

	@Test
	@DisplayName("prueba de GetPrecio con un combo vacio")
	public void testGetPrecioComboVacio() {
		Combo vacio = new Combo("combo gratis", 1, new ArrayList<ProductoMenu>());
		assertEquals(0, vacio.getPrecio(),
				"El precio del combo con 0 de descuento debe ser la suma del precio de los items.");
	}

	@Test
	@DisplayName("Agregar un item a la lista original no deberia cambiar el precio del combo")
	public void testGetPrecioItemNuevo() {
		items.add(new ProductoMenu("extra", 1000));
		assertEquals(22050, combo.getPrecio(),
				"El precio del combo no deberia cambiar si se suma un item nuevo a la lista original.");
	}

	@Test
	@DisplayName("prueba de texto de la factura")
	public void testGeneralTextoFactura() {
		String esperado = "Combo combo corral\n" + " Descuento: 0.1\n" + "            22050\n";
		assertEquals(esperado, combo.generarTextoFactura(), "El texto de la factura del combo no es el esperado.");

	}

}
