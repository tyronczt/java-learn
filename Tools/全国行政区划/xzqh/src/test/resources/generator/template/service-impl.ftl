package ${servicePackage}.impl;

import ${entityPackage}.${modelNameUpperCamel};
import ${mapperPackage}.${modelNameUpperCamel}Mapper;
import ${servicePackage}.${modelNameUpperCamel}Service;
import ${basePackage}.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
* @Description: ${description}控制类
* @Author: ${author}
*
* @Date: Created in ${date}
*/
@Service
@Transactional
public class ${modelNameUpperCamel}ServiceImpl extends AbstractService<${modelNameUpperCamel}> implements ${modelNameUpperCamel}Service {
    @Resource
    private ${modelNameUpperCamel}Mapper ${modelNameLowerCamel}Mapper;

}
