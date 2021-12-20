layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var  formSelects = layui.formSelects;


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //线的到当前iframe层的索引
        parent.layer.close(index); //在执行关闭

    });



    /**
     * 1.配置远程搜索 请求头 请求参数 请求类型等
     *
     * formSelects.config(ID,Options,isJson)
     *
     * @param Id   xm-select的值
     * @param Options 配置项
     * @param isJson 是否传输json数据 true将添加请求头 Content-type:application/json; charset=UTF-8
     */
    var userId=$("input[name='id']").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx+"/role/queryAllRoles?userId="+userId,
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);



    form.on('submit(addOrUpdateUser)',function (data) {
        var index= top.layer.msg("数据提交中,请稍后...",{icon:16,time:false,shade:0.8});
        var url = ctx+"/user/add";
        if($("input[name='id']").val()){
            url=ctx+"/user/update";
        }
        $.post(url,data.field,function (res) {
            if(res.code==200){
                top.layer.msg("操作成功");
                top.layer.close(index);
                layer.closeAll("iframe");
                // 刷新父页面
                parent.location.reload();
            }else{
                layer.msg(res.msg);
            }
        });
        return false;
    });





});