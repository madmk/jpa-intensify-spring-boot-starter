# 用于对 spring boot jpa 进行加强

### 已实现功能：
 1. 自定义 `新增拦截器` `修改拦截器` `查询拦截器` `删除拦截器`
 2. 逻辑删除控制器

### 使用方式

#### 准备
ps: 未添加mvaen库中 需手动添加自己私有或本地mvaen库中

mvaen 中引入如下
```xml
  <dependency>
    <groupId>com.madmk</groupId>
    <artifactId>jpa-intensify-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
```

需要进行控制或拦截的 `repository` 需继承 `LogicRepository<T,ID>>` 

`LogicRepository<T,ID>>` 接口中新增的 以`logic`进行开头的方法 

所有拦截和增强都主要围绕 `logic`进行开头的方法 进行

其最大限度的保证了在不影响原`spring boot jpa`实现的情况下对`spring boot jpa`进行扩展

#### 逻辑删除控制
 在删除字段上添加注解 `@DelSign` 如下：
 ```java
@DelSign
private short del;
 ```
现支持类型有 `int`,`byte`,`short`,`long`,`char` 及其包装类型 逻辑删除取值分别是存在为`1` 删除时置为`0`

之后调用 `LogicRepository<T,ID>>` 接口的 `save`和以 `logic` 开头的方法进行增删改查等操作将自动对逻辑删除进行判断处理

#### 自定义拦截器使用方式

1. 继承 `AbstractLogicIntensifyProcessor<T>` 抽象类。

* 通过方法`isSustain` 返回值判断某实体是否使用此扩展

* 通过方法`persistEnhance` 可以对新增前的实体进行修改

* 通过方法`mergeEnhance` 可以对修改前的实体进行修改

* 通过方法`delEnhance` 可以对删除时进行条件修改或禁止删除并更改返回值

* 通过方法`queryEnhance` 可以对查询时实体进行条件修改并更改返回值

2. 需注入`bean`如下
以逻辑删除为例：
```java
    @Bean
    public LogicIntensifyProcessorFiltrate logicIntensifyProcessorFiltrate(EntityManager em){
        LogicIntensifyProcessorFiltrate logicIntensifyProcessorFiltrate=new LogicIntensifyProcessorFiltrate(em);
        logicIntensifyProcessorFiltrate.addLogicIntensifyProcessor(e->new DelLogicIntensifyProcessor(e,em))
        return logicIntensifyProcessorFiltrate;
    }
```
ps：逻辑删除在原代码中已经默认添加 不需要再次向 `LogicIntensifyProcessorFiltrate` 添加，这里仅做示例。

详细拦截器使用示例请参见 `DelLogicIntensifyProcessor<T>`类。

 
