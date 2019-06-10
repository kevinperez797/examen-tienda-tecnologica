package dominio;

import dominio.repositorio.RepositorioProducto;

import java.util.Calendar;
import java.util.Date;


import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;


public class Vendedor {

	/* Definir las constantes que vamos a utilizar */
    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String NO_HAY_COD_NI_NOMBRE = "El producto no tiene codigo o no hay nombre del cliente, por lo tanto no se puede generar la garantia";
    public static final String PORDUCTO_NO_TIENE_GARANTIA = "El producto no cuenta con garantia extendida";
    //Condiciones por si el valor del prodcuto es mayor a 500.000
    public static final double PORCENTAJE_MAYOR_A_500 = 0.20;
    public static final int DIAS_GARANTIA_MAYOR_A_500 = 200;
    //Condiciones por si el valor del prodcuto es mayor a 500.000
    public static final double PORCENTAJE_MENOR_A_500 = 0.10;
    public static final int DIAS_GARANTIA_MENOR_A_500 = 100;
    public static final double VALOR_PRODUCTO_CONDICION = 500000.0 ;

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }
    
    /*
     * OBJETIVO: Genera la garantia acorde a las condiciones estipuladas
     * CONTICIONES: -Producto no puede tener mas de una garantia 
     * 				-Si el codigo del producto tiene 3 vocales este no tendrá garantia extendida
     * 				-si el costo del producto es mayor a 500.000 la garantia sera el 20% del costo del producto y se acabara en 200 dias
     * 				-si el costo del producto es menor a 500.000 la garantia sera el 10% del costo del producto y se acabara en 100 dias.
     * 				-NO CUENTAN LOS DIAS LUNES Y DOMINGOS.
     * ENTRADA:- codigoProdcuto y nombreCliente
     * 
     * */
    public GarantiaExtendida generarGarantia(String codigoProducto, String nombreCliente) {
    	Producto producto = null ;
    	// días que han transcurrido al generar la factura, se incluye el dia que es generada (1)
    	int diasTranscurridos = 1;
    	//Los dias de garantia que se les asigna acorde al precio del producto
        int diasGarantia = 0; 
        //Es la fecha la cual se termina la garantia 
		Date fechaFinalGarantia = null ;
		//fecha en la que la garantia se genera
		Date fechaInicialGarantia = null ; 
		//Es el precio de la garantia 
		double precioGarantia = 0.0 ;
		
		if ( repositorioProducto.codigoTieneTresVocales(codigoProducto) ) {
			throw new GarantiaExtendidaException(Vendedor.PORDUCTO_NO_TIENE_GARANTIA); 
		}
		
		//Verificamos que el codigo del producto o el nombre del cliente exista
		if ( (codigoProducto.isEmpty()) || (nombreCliente.isEmpty()) ) {
			throw new GarantiaExtendidaException(Vendedor.NO_HAY_COD_NI_NOMBRE);
		}
		
		if ( this.repositorioGarantia.tieneGarantia(codigoProducto) ) {
			throw new GarantiaExtendidaException(Vendedor.EL_PRODUCTO_TIENE_GARANTIA);
		}	
		
		/*
		 * Verificamos si el dia en el que fue generada la factura es lunes o domingo 
		 */
		//Obtenemos el codigo del producto y lo asignamos a la variable "producto"
		producto = this.repositorioProducto.obtenerPorCodigo(codigoProducto);
        Calendar diaActual = Calendar.getInstance();
        fechaInicialGarantia = diaActual.getTime() ;
        
    	if ( (diaActual.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY) || (diaActual.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY) ) {
    		diasTranscurridos = 0;
    	}
    	
    	/*
    	 * Deacuerdo al valor del prodcuto le asignamos los dias y el porcentaje de la garantia extendida.
    	 */
    	if ( producto.getPrecio() > Vendedor.VALOR_PRODUCTO_CONDICION ) { 
    		diasGarantia = Vendedor.DIAS_GARANTIA_MAYOR_A_500 ; 
    		precioGarantia = producto.getPrecio() * ( Vendedor.PORCENTAJE_MAYOR_A_500) ;
    		    		
    	}
    	else {
    		diasGarantia = Vendedor.DIAS_GARANTIA_MENOR_A_500 ;
    		precioGarantia = producto.getPrecio() * ( Vendedor.PORCENTAJE_MENOR_A_500) ;    		
    	}
    	
    	/*
    	 * Teniendo en cuenta de que no se contaran los lunes, a la variable 'diasTranscurridos' le sumaremos en el calendario 
    	 */
		while ( diasTranscurridos < diasGarantia) {
			diaActual.add(Calendar.DAY_OF_MONTH, 1);
	    	if ( diaActual.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY ) {
	    		diasTranscurridos++;
	    	}
		}
		
		/*
		 * Verificamos si el dia de finalizacion de la garantia finaliza un domingo y si es asi adicionamos dos dias mas al calendario ya que no se tiene en cuenta los lunes
		 */
    	if ( diaActual.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY ) {
    		 diaActual.add(Calendar.DAY_OF_MONTH, 2);
    	}
    	
    	fechaFinalGarantia = diaActual.getTime() ;
    	
	    GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto, fechaInicialGarantia, fechaFinalGarantia,
	            precioGarantia, nombreCliente);
		
		repositorioGarantia.agregar(garantiaExtendida);
		
        return garantiaExtendida;
    }
    
    public boolean tieneGarantia(String codigoProducto) {
    	Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigoProducto);
    	if(producto != null){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public void crearProducto(Producto producto) {
    	this.repositorioProducto.agregar(producto);	
    }


}
