package com.example.bibliotech.service;

import com.example.bibliotech.dao.PaymentMethodDao;
import com.example.bibliotech.exception.DatabaseException;
import com.example.bibliotech.model.PaymentMethods;

import java.util.List;

public class PaymentMethodService {
    private PaymentMethodDao paymentMethodDao = new PaymentMethodDao();

    // Lấy tất cả phương thức thanh toán
    public List<PaymentMethods> getAllPaymentMethods() throws DatabaseException {
        return paymentMethodDao.getAllPaymentMethods();
    }

    // Thêm phương thức thanh toán mới
    public void addPaymentMethod(PaymentMethods paymentMethod) throws DatabaseException {
        paymentMethodDao.addPaymentMethod(paymentMethod);
    }

    // Lấy phương thức thanh toán theo ID
    public PaymentMethods getPaymentMethodById(int paymentMethodId) throws DatabaseException {
        return paymentMethodDao.getPaymentMethodById(paymentMethodId);
    }
}
