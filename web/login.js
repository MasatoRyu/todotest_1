// 繝輔ぅ繝ｼ繝ｫ繝峨′螟画峩縺輔ｌ縺溷�ｴ蜷医↓蜃ｦ逅�☆繧矩未謨ｰ
function fieldChanged(){
    var userId = getField("user_id");
    var password = getField("password");
    var disabled = true;
    
    if (userId.value.length > 0 && password.value.length > 0) {
        disabled = false;
    }
    
    var login = getField("login");
    if (disabled) {
        login.setAttribute("disabled", "disabled");
    }
    else {
        login.removeAttribute("disabled");
    }
}

// 繝輔ぅ繝ｼ繝ｫ繝峨ｒ蜿門ｾ励☆繧矩未謨ｰ
function getField(fieldName){
    var field = document.getElementById(fieldName);
    if (field == undefined) {
        throw new Error("隕∫ｴ�縺瑚ｦ九▽縺九ｊ縺ｾ縺帙ｓ: " + fieldName);
    }
    return field;
}