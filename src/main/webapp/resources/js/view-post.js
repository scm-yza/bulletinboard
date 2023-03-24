$(document).ready(function() {
	displayPostList();
});

function displayPostList() {
	var root = $("#postListBody");
	list.forEach((item, index) => {
		// Data to Display
		var dataRow = "<tr>"
			+ '<td>' + (index + 1) + '</td>'
			+ '<td>' + item.title + "</td>"
			+ '<td style="word-break: break-all;"><p style="margin: 0;">' + item.description + "</p></td>";
		// Controls
		dataRow += getStatusBtn(index);
		dataRow += '<td><button class="btn btn-primary w-100" type="button" data-toggle="modal" data-target="#editPostModal" onclick="openEditForm(' + index + ')">Edit</button></td>'
			+ '<td><button class="btn btn-danger w-100" type="button" data-toggle="modal" data-target="#confirmModal" onclick="deletePost(' + index + ')">Delete</button></td>';
		dataRow += "</tr>";
		root.append(dataRow);
	})
}

function getStatusBtn(index) {
	var status = list[index].status;
	var btnStyle = 'btn-status btn w-100 ' + (status ? 'btn-success' : "") + '';
	var btnText = status ? "Active" : "Disable";
	return '<td><button type="button" class="' + btnStyle + '" onclick="updatePostStatus(' + index + ')">' + btnText + '</button></td>';
}

function openEditForm(index) {
	setFormValues(index);
	$("#txtFlag").val(0);
}

function updatePostStatus(index) {
	setFormValues(index);
	$("#txtFlag").val(1);
	$('#postEditForm [formaction="update"]').click();
}

function setFormValues(index) {
	var post = list[index];
	$("#txtId").val(post.id);
	$("#txtTitle").val(post.title);
	$("#txtDescription").val(post.description);
	$("#chkBxStatus").val(post.status);
}

function deletePost(index) {
	var id = list[index].id;
	$("#btnConfirm").off("click").click(function() {		
		$("#txtId").val(id);
    	$('#postEditForm [formaction="delete"]').click();
		$("#confirmModal").modal("hide");	
	});
} 