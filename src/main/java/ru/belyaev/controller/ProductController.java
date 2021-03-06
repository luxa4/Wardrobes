/*
 * Created by Vologda Developer
 * Date: 25.06.2020
 * Time: 14:48
 */


package ru.belyaev.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import ru.belyaev.constant.SessionConstant;
import ru.belyaev.entity.Order;
import ru.belyaev.entity.Product;
import ru.belyaev.entity.ShoppingCart;
import ru.belyaev.entity.User;
import ru.belyaev.service.OrderService;
import ru.belyaev.service.ProductService;
import ru.belyaev.service.ShoppingCartService;
import ru.belyaev.service.UserService;
import ru.belyaev.util.SessionUtil;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class ProductController {

    public static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ServletContext servletContext;

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    ShoppingCartService shoppingCartService;

    @GetMapping("/")
    public String showAllProduct(Model model, HttpSession session) {
        User user = userService.getUser();
        boolean existOrders = orderService.existOrdersByUser(user);
        if (existOrders) {
            List<Order> orderList = orderService.findUserOrders(user);
            SessionUtil.setListOrder(session, orderList);
        } else {
            SessionUtil.setListOrder(session, null);
        }
        List<Product> productList = productService.listAllProducts();
        ShoppingCart shoppingCart = shoppingCartService.getShoppingCartByUser(user);

        Long countProduct   = productService.countAllProduct();
        BigDecimal maxLen   = productService.showMaxLength();
        BigDecimal minLen   = productService.showMinLength();
        BigDecimal maxHei   = productService.showMaxHeight();
        BigDecimal minHei   = productService.showMinHeight();
        BigDecimal maxWid   = productService.showMaxWidth();
        BigDecimal minWid   = productService.showMinWidth();
        BigDecimal maxPrice = productService.showMaxPrice();
        BigDecimal minPrice = productService.showMinPrice();

        servletContext.setAttribute(SessionConstant.MAX_LEN.toString(), maxLen.toPlainString());
        servletContext.setAttribute(SessionConstant.MIN_LEN.toString(), minLen.toPlainString());
        servletContext.setAttribute(SessionConstant.MAX_HEI.toString(), maxHei.toPlainString());
        servletContext.setAttribute(SessionConstant.MIN_HEI.toString(), minHei.toPlainString());
        servletContext.setAttribute(SessionConstant.MAX_WID.toString(), maxWid.toPlainString());
        servletContext.setAttribute(SessionConstant.MIN_WID.toString(), minWid.toPlainString());
        servletContext.setAttribute(SessionConstant.MAX_PRICE.toString(), maxPrice.toPlainString());
        servletContext.setAttribute(SessionConstant.MIN_PRICE.toString(), minPrice.toPlainString());
        servletContext.setAttribute(SessionConstant.COUNT_PRODUCT.toString(), countProduct);

        model.addAttribute(SessionConstant.PRODUCT_LIST.toString(), productList);
        SessionUtil.setCurrentShoppingCart(session, shoppingCart);
        SessionUtil.setCurrentUser(session, user);
        return "home";
    }

    @GetMapping("/product/{productId}")
    public String showPersonalProductPage(@PathVariable int productId, Model model) {
        Product product = productService.showProductPageByProductId(productId);
        model.addAttribute(SessionConstant.PRODUCT.toString(), product);
        return "oneProductPage";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletResponse resp) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("status", resp.SC_NOT_FOUND);
        System.out.println(resp.getStatus());
        mav.setViewName("errorPage");
        return mav;
    }
}
