$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title=$("#recipient-name").val();
	var content=$("#message-text").val();
	//发布异步请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title":title,"content":content},
		function (data){
			data=$.parseJSON(data);
		//提示框中返回信息
			$("#hintBody").text(data.msg);
		//显示提示框
			$("#hintModal").modal("show");
		//2s后隐藏提示框
			setTimeout(function(){
				if(data.code==0){
					window.location.reload();
				}
				$("#hintModal").modal("hide");
			}, 2000);
		}
	)

}