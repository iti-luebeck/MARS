function si_captcha_refresh(img_id,form_id,type,securimage_url,securimage_show_url) {
   var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
   var string_length = 16;
   var prefix = '';
   for (var i=0; i<string_length; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		prefix += chars.substring(rnum,rnum+1);
   }
  document.getElementById('si_code_' + form_id).value = prefix;

  var si_image_ctf = securimage_show_url + prefix;
  if(img_id == 'si_image_side_login') {
       document.getElementById('si_image_side_login').src = si_image_ctf;
  }else{
       document.getElementById('si_image_' + form_id).src = si_image_ctf;
  }

  if(type == 'flash') {
    var si_flash_url = securimage_url+'/securimage_play.swf?prefix='+prefix+'&bgColor1=#8E9CB6&bgColor2=#fff&iconColor=#000&roundedCorner=5&audio='+securimage_url+'/securimage_play.php?prefix='+prefix;
    var si_flash = '<object type="application/x-shockwave-flash" data="'+si_flash_url+'" id="SecurImage_as3_'+form_id+'" width="19" height="19">';
   	    si_flash += '<param name="allowScriptAccess" value="sameDomain" />';
   	    si_flash += '<param name="allowFullScreen" value="false" />';
   	    si_flash += '<param name="movie" value="'+si_flash_url+'" />';
  	    si_flash += '<param name="quality" value="high" />';
  	    si_flash += '<param name="bgcolor" value="#ffffff" />';
  	    si_flash += '</object>';
       document.getElementById('si_flash_' + form_id).innerHTML = si_flash;
       return false;
  } else {
   var si_aud = securimage_url+'/securimage_play.php?prefix='+prefix;
    document.getElementById('si_aud_' + form_id).href = si_aud;
   }
}