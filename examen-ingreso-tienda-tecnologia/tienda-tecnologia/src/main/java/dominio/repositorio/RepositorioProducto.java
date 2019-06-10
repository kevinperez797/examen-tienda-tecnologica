package dominio.repositorio;

import dominio.Producto;

public interface RepositorioProducto {

	/**
	 * Permite obtener un producto dado un codigo
	 * @param codigo
	 * @return
	 */
	Producto obtenerPorCodigo(String codigo);

	/**
	 * Permite agregar un producto al repositorio
	 * @param producto
	 */
	void agregar(Producto producto);
	
	/**
	 * Permite determinar si el string del codigo del producto tiene 3 vocales. 
	 * @param producto
	 */
	boolean codigoTieneTresVocales(String codigoProducto);

}