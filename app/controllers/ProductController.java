package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import views.html.signup;
import views.html.userProfile;

import java.util.Collections;
import java.util.Date;

import static play.data.Form.form;
import play.mvc.Controller;

/**
 * @author Gøran Schumacher (GS) / Schumacher Consulting Aps
 * @version $Revision$ 19/01/15
 */
public class ProductController extends Controller {

    public Result createProducts() {
        RecurringProduct rProd = new RecurringProduct();
        rProd.description="Månedskort";
        rProd.amount=32000L;
        rProd.vatAmount=8000L;
        rProd.type=RecurringProduct.Type.MONTH;
        rProd.period=RecurringProduct.Period.MONTH;
        rProd.save();
        //
        CounterProduct cProd = new CounterProduct();
        cProd.amount=800L;
        cProd.vatAmount=200L;
        cProd.description="Coke Zero, 0.5L";
        cProd.eanCode="12345678901234";
        cProd.save();
        return ok(Json.toJson(cProd));
    }

    public Result addRecurringPurchase() {
        User user = User.findByEmail("schumacher@me.com");
        RecurringProduct rProd = RecurringProduct.findById(1L);
        UserRecurringPurchase rPurch = new UserRecurringPurchase();
        rPurch.save();
        rPurch.product =rProd;
        UserRecurringPurchaseItem item = rPurch.createNewItem();
        item.save();
        return ok(Json.toJson(item));
    }
}
