$(function () {
    loadModuleInfo(); 
});


var zTreeObj;
function loadModuleInfo() {
    $.ajax({
        type:"post",
        url:ctx+"/module/queryAllModules?roleId="+$("input[name='roleId']").val(),
        dataType:"json",
        success:function (data) {

            // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                check: {
                    enable: true
                },
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                //绑定函数
                callback: {
                    //当checkbox/radio 被选中或取消选中时触发的函数
                    onCheck: zTreeOnCheck
                }
            };
            zTreeObj = $.fn.zTree.init($("#test1"), setting, data);
        }
    })
}


function zTreeOnCheck(event, treeId, treeNode) {
    //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
    // getCheckedNodes 获取所有被勾选的节点集合 如果Checked=true 表示获取勾选的节点 反之就是获取未勾选的节点
    var nodes= zTreeObj.getCheckedNodes(true);
    //获取所有的资源的id值 mIds=1&mIds=2&
    //判断并遍历选中的节点集合
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }

    //获取需要授权的角色ID的值(隐藏域)
    //发送ajax请求 执行角色的授权操作
    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+$("input[name='roleId']").val(),
        dataType:"json",
        success:function (data) {
            console.log(data);
        }
    })

}