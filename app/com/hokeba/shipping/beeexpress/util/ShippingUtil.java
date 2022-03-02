package com.hokeba.shipping.beeexpress.util;

import com.hokeba.shipping.beeexpress.BeeExpressService;
import com.hokeba.shipping.beeexpress.response.PriceResponse;
import models.CourierPointLocation;
import models.Product;

import java.util.*;

/**
 * Created by hendriksaragih on 8/14/17.
 */
public class ShippingUtil {
    private static List<Boxes> boxes = new LinkedList<>(Arrays.asList(
            new Boxes(27.94, 17.78, 10.16),
            new Boxes(30.48, 30.48, 20.32),
            new Boxes(40.64, 30.48, 30.48)
    ));

    private static boolean isFillBoxes(Boxes product, Boxes pack){
        return (
                Math.floor(pack.getLength() / product.getLength()) *
                Math.floor(pack.getWidth() / product.getWidth()) *
                Math.floor(pack.getHeight() / product.getHeight())
        ) >= 1;
    }

    public static Double calculateCost(Map<Product, Integer> items, CourierPointLocation from, CourierPointLocation to){
        Map<Integer, Integer> boxes = new HashMap<>();
        items.forEach((k,v)->{
            Integer idx = findBoxes(k.getBoxes());
            Integer count = 0;
            if (boxes.containsKey(idx)){
                count = boxes.get(idx);
            }
            boxes.put(idx, count + v);
        });

        return getPrice(boxes, from.township.name, from.agentId, to.township.name, to.agentId);
    }

    private static Double getPrice(Map<Integer, Integer> boxes, String originTown, int originPoint, String destTown, int destPoint){
        final Double[] prices = {0D};
        boxes.forEach((k,v)->{
            PriceResponse result = BeeExpressService.getInstance().getPrice(originTown, originPoint, destTown, destPoint, k);
            if (result == null){
                result = BeeExpressService.getInstance().getPrice(destTown, destPoint, originTown, originPoint, k);
                if (result != null){
                    prices[0] += result.getPrice() * v;
                }
            }else{
                prices[0] += result.getPrice() * v;
            }

        });

        return prices[0];
    }

    private static Integer findBoxes(Boxes product){
        int idx = 0;
        for (Boxes b : boxes){
            if (isFillBoxes(product, b)){
                break;
            }
            idx += 1;
        }

        return idx + 2;
    }

    public static Boxes findBeeBoxes(Boxes product){
        Boxes tmp = null;
        for (Boxes b : boxes){
            if (isFillBoxes(product, b)){
                tmp = b;
                break;
            }
        }
        return tmp;
    }
}
