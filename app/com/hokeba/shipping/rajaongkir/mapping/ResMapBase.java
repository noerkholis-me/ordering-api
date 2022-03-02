package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapBase {

	@JsonProperty("rajaongkir")
	ResMapRajaOngkir rajaongkir;
	
//	public void setX(Object a) {
//		this.rajaongkir.setBaseResponse(a);
//	}

	public ResMapRajaOngkir getRajaongkir() {
		return rajaongkir;
	}

	public void setRajaongkir(ResMapRajaOngkir rajaongkir) {
		this.rajaongkir = rajaongkir;
	}
	
	
}

//{
//    "rajaongkir": {
//        "query": {
//            "id": "12"
//        },
//        "status": {
//            "code": 200,
//            "description": "OK"
//        },
//        "results": {
//           "province_id": "12",
//           "province": "Kalimantan Barat"
//        }
//    }
//}