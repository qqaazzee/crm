layui.use(['table','layer'],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });



    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        var index = parent.layer.getFrameIndex(window.name); //线的到当前iframe层的索引
        parent.layer.close(index); //在执行关闭

    });


    // 多条件搜索
    $(".search_btn").on("click",function () {
        table.reload("roleListTable",{
            page:{
                curr:1
            },
            where:{
                // 角色名
                roleName:$("input[name='roleName']").val()
            }
        })
    });

    // 头工具栏事件
    table.on('toolbar(roles)',function (obj) {
        switch (obj.event) {
            case "add":
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                openAddGrantDialog(table.checkStatus(obj.config.id).data);
                break;
        }
    });


    table.on('tool(roles)',function (obj) {
        var layEvent =obj.event;
        if(layEvent === "edit"){
            openAddOrUpdateRoleDialog(obj.data.id);
        }else if(layEvent === "del"){
            layer.confirm("确认删除当前记录?",{icon: 3, title: "角色管理"},function (index) {
                $.post(ctx+"/role/delete",{roleId:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("删除成功");
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg);
                    }
                })
            })
        }
    });




    function openAddOrUpdateRoleDialog(id) {
        var title="角色管理-角色添加";
        var url=ctx+"/role/addOrUpdateRolePage";
        if(id){
            title="角色管理-角色更新";
            url=url+"?roleId="+id;
        }
        layui.layer.open({
            title:title,
            type:2,
            area:["400px","300px"],
            maxmin:true,
            content:url
        })
    }


    function openAddGrantDialog(datas) {
        if(datas.length==0){
            layer.msg("请选择待授权的角色记录!",{icon:5});
            return;
        }
        if(datas.length>1){
            layer.msg("暂不支持批量角色授权!",{icon:5});
            return;
        }

        layui.layer.open({
            title:"角色管理-角色授权",
            type:2,
            area:["600px","600px"],
            maxmin:true,
            content:ctx+"/module/toAddGrantPage?roleId="+datas[0].id
        })

    }



});
