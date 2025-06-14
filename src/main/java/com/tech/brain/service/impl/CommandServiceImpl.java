package com.tech.brain.service.impl;

import com.tech.brain.entity.ProductEntity;
import com.tech.brain.exception.ErrorCode;
import com.tech.brain.exception.ErrorSeverity;
import com.tech.brain.exception.CommandException;
import com.tech.brain.model.Product;
import com.tech.brain.model.ProductEvent;
import com.tech.brain.publisher.EventPublisher;
import com.tech.brain.repository.CommandRepository;
import com.tech.brain.service.CommandService;
import com.tech.brain.utils.JSONUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Transactional
@Service
public class CommandServiceImpl implements CommandService {

    private final CommandRepository commandRepository;
    private final EventPublisher eventPublisher;
    private final JSONUtils jsonUtils;

    public CommandServiceImpl(CommandRepository queryRepository, EventPublisher eventPublisher, JSONUtils jsonUtils) {
        this.commandRepository = queryRepository;
        this.eventPublisher = eventPublisher;
        this.jsonUtils = jsonUtils;
    }

    @Override
    public Product createProduct(Product product) {
        log.info("Creating new product=[{}]", jsonUtils.javaToJSON(product));
        ProductEntity[] products = new ProductEntity[1];
        commandRepository.findByProductCode(product.getProductCode()).ifPresentOrElse(existingProduct -> {
            log.error("Duplicate product code=[{}]", product.getProductCode());
            throw new CommandException(ErrorCode.ERR009.getErrorCode(), ErrorSeverity.DEBUG,
                    ErrorCode.ERR009.getErrorMessage());

        }, ()->{
            ProductEntity entity = new ProductEntity();
            BeanUtils.copyProperties(product, entity);
            products[0] = commandRepository.save(entity);
        });
        BeanUtils.copyProperties(products[0], product);
        ProductEvent event = new ProductEvent("CREATE_EVENT", product);
        eventPublisher.publish(event);
        return product;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        log.info("Updating product=[{}]", jsonUtils.javaToJSON(product));
        ProductEntity[] products = new ProductEntity[1];
        commandRepository.findById(id).ifPresentOrElse(existingProduct -> {
            log.info("Updating product id=[{}]", id);
            copyNonNullProperties(product, existingProduct);
            products[0] = commandRepository.save(existingProduct);
        }, () -> {
            log.error("Product id=[{}] not found", id);
            throw new CommandException(ErrorCode.ERR002.getErrorCode(), ErrorSeverity.FATAL,
                    ErrorCode.ERR002.getErrorMessage());
        });
        BeanUtils.copyProperties(products[0], product);
        ProductEvent event = new ProductEvent("UPDATE_EVENT", product);
        eventPublisher.publish(event);
        return product;
    }

    @Override
    public String deleteProduct(Long id) {
        log.info("Deleting product id=[{}]", id);
        AtomicReference<String> response = new AtomicReference<>("");
        commandRepository.findById(id).ifPresentOrElse(existingProduct -> {
            log.info("Deleting product = [{}]", jsonUtils.javaToJSON(existingProduct));
            Product product = new Product();
            BeanUtils.copyProperties(existingProduct, product);
            ProductEvent event = new ProductEvent("DELETE_EVENT", product);
            commandRepository.deleteById(id);
            eventPublisher.publish(event);
            response.set("Deleted product " + id);
        }, ()-> {
            log.info("Product id=[{}] not found", id);
            response.set("Deletion failed product: " + id + " May be there is no product with this id");
        });

        return response.get();
    }


}
