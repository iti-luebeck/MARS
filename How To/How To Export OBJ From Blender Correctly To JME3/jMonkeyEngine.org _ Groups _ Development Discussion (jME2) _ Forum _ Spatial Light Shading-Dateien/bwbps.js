var $j = jQuery.noConflict();
var bwbpsActiveGallery = 0;
var displayedGalleries = "";
var bwbpsUploadStatus = false;
var bwbpsSelectedImages;
var bwbpsStopResizing = false;
var bwbpsAllImport = 0; //variable for handling Admin Import toggling

$j.fn.tagName = function() {
    return this.get(0).tagName;
}


// JavaScript Document
/*************************************************
	PhotoSmash JS
/*************************************************/

$j(document).ready(function() { 
	//Show and hide the Loading icon on Ajax start/end
	
	/*
	$j("#bwbps_loading")
	.ajaxStart(function(){
		$j(this).show();
	})
	.ajaxComplete(function(){
		$j(this).hide();
		if(	bwbpsUploadStatus == false)
			{
				$j("#bwbps_submitBtn").removeAttr('disabled');
				$j("#bwbps_imgcaptionInput").removeAttr('disabled');
				$j('#bwbps_message').html("Image upload failed. Your image may have been too large or there may have been another problem. Please reload the page and try again.");
			}
		bwbpsUploadStatus = false;
	});
	
	*/
	
	$j('.bwbps_uploadform').submit(function() { 
		$j('#bwbps_message').html('');
		bwbpsAjaxLoadImage(this);
		return false; 
	});
	
	//make sure the upload form radio button is on Select file
	$j(".init_radio").attr("checked","checked");
	
	
	//Add OnClick to the Mass Update Buttons in the PhotoSmash Settings form
	if($j('#bwbps_gen_settingsform').val() == '1'){
		
		bwbpsAddPSSettingsMassUpdateActions();
	}
	
	$j('.bwbps-post-cat-form').attr('multiple','multiple');
	
});




// User Favorites Image
function bwbpsSaveFavorite(image_id, fav_nonce){

	var _data = {};
	
	_data['action'] = 'favoriteimage';
	
	_data['image_id'] = image_id;
	
	_data['_wpnonce'] = fav_nonce;
		
	try{
		$j('#ps_savemsg').show();
	}catch(err){}
	
	if(jQuery(".bwbps-fav-" + image_id).hasClass('bwbps-fav-0'))
	{
		jQuery(".bwbps-fav-" + image_id).removeClass('bwbps-fav-0');
		jQuery(".bwbps-fav-" + image_id).addClass('bwbps-fav-1');
	} else {
		jQuery(".bwbps-fav-" + image_id).removeClass('bwbps-fav-1');
		jQuery(".bwbps-fav-" + image_id).addClass('bwbps-fav-0');
	}
	
	$j.ajax({
		type: 'POST',
		url: bwbpsAjaxRateImage,
		data : _data,
		dataType: 'json',
		success: function(data) {
			bwbpsFavoriteSuccess(data, image_id);
		}
	});
	
	return false;

}

function bwbpsFavoriteSuccess(data, image_id){
	if(data == -1){
		alert('Security failed: nonce.');
		return;
	}
	
	if(data.status == 1)
	{
		if(jQuery(".bwbps-fav-" + image_id).hasClass('bwbps-fav-0')){
			jQuery(".bwbps-fav-" + image_id).removeClass('bwbps-fav-0');
			jQuery(".bwbps-fav-" + image_id).addClass('bwbps-fav-1');
		}
	} else {
		if(jQuery(".bwbps-fav-" + image_id).hasClass('bwbps-fav-1')){
			jQuery(".bwbps-fav-" + image_id).removeClass('bwbps-fav-1');
			jQuery(".bwbps-fav-" + image_id).addClass('bwbps-fav-0');
		}
	}
}

// User Delete Own Images...only will delete if image is not approved
function bwbpsUserDeleteImage(image_id){

	var _data = {};
	
	if(!confirm('Do you wish to permanently delete this image?')){ return; }
	
	_data['action'] = 'userdeletewithpost';
	
	_data['image_id'] = image_id;
	
	_data['_ajax_nonce'] = bwbps_upload_nonce;
		
	try{
		$j('#ps_savemsg').show();
	}catch(err){}
	
	jQuery(".bwbps_delbtn_" + image_id).after("<span style='color: red; font-size: .8em;' class='bwbps_deleting_" + image_id +"'>deleting...</span>");
	jQuery(".bwbps_delbtn_" + image_id).remove();
	
	$j.ajax({
		type: 'POST',
		url: bwbpsAjaxUserURL,
		data : _data,
		dataType: 'json',
		success: function(data) {
			bwbpsUserDeleteImageSuccess(data, image_id);
		}
	});
	
	return false;

}

function bwbpsUserDeleteImageSuccess(data, image_id){
	if(data == -1){
		alert('Security failed: nonce.');
		return;
	}
	
	if(data.status == 1)
	{
		jQuery(".bwbps_deleting_" + image_id).html('deleted');
	}
}



// Upload Image by Ajax
function bwbpsAjaxLoadImage(myForm){

	//Get the Form Prefix...this is needed due to multiple forms being possible
	var form_pfx = myForm.id;
		
	form_pfx = form_pfx.replace("bwbps_uploadform", "");
	
	$j('#' + form_pfx + 'bwbps_imgcaption').val($j('#' + form_pfx + 'bwbps_imgcaptionInput').val());
	
	//Show the loader image
	$j('#' + form_pfx + 'bwbps_result').html('');
	$j('#' + form_pfx + 'bwbps_message').html('');
	$j('#' + form_pfx + 'bwbps_previewpost').html('');
	$j("#" + form_pfx + "bwbps_loading").show();
	/*
	$j("#" + form_pfx + "bwbps_loading").ajaxComplete(function(){
		$j(this).hide();
	});
	*/
	
	var options = { 
		beforeSubmit:  function(){ 
			if(!bwbpsVerifyUploadRequest(form_pfx)){
				$j("#" + form_pfx + "bwbps_loading").hide();
				return false;
			} else { return true; } 
		},
		success: function(data, statusText){ bwbpsUploadSuccess(data, statusText, form_pfx); } , 
		failure: function(){ 
			alert('failed');
			$j("#" + form_pfx + "bwbps_loading").hide(); 
			bwbpsUploadStatus = false;
			},
		url:      bwbpsAjaxUpload,
		iframe: true,
		dataType:  'json'
	}; 
		
	//Submit that baby
	$j(myForm).ajaxSubmit(options); 
	return false;
}

$j(window).bind("load",  psSetGalleryHts);

function psSetGalleryHts(){
	if(displayedGalleries == '') return;
	var ps = displayedGalleries.split('|');
	
	var i=0;
	var icnt = ps.length;
	for(i=0;i < icnt;i++){
		if(ps[i] != ""){
			bwbps_equalHeight($j(".psgal_" + ps[i]));
		}
	}
}

function bwbpsVerifyUploadRequest(form_pfx) { 				
		
	if ( !bwbpsVerifyFileFilled(form_pfx) ) { 
		$j('#' + form_pfx + 'bwbps_message').html('<b>VALIDATION ERROR: Please select a file.</b>');
		
		$j('#' + form_pfx + 'bwbps_submitBtn').removeAttr('disabled');
		$j('#' + form_pfx + 'bwbps_imgcaptionInput').removeAttr('disabled');
		return false; 
	} 

	$j('#' + form_pfx + 'bwbps_submitBtn').attr('disabled','disabled');

	$j('#' + form_pfx + 'bwbps_imgcaptionInput').attr('disabled','disabled');
	$j('#' + form_pfx + 'bwbps_result').html('');

	return true;
} 


/*
 *	Determine if the File Field is required, and if so, is it filled
 *
 */
function bwbpsVerifyFileFilled(form_pfx){
	if( $j('#' + form_pfx + 'bwbps_allownoimg').val() == 1 ){ return true; }
	var filetype = $j('#' + form_pfx + 'bwbps_uploadform :input:radio[name=bwbps_filetype]:checked').val();
	
	var bFilled = false;
	
	switch (Number(filetype)){
		case 0 :	//Image
			bFilled = $j('#' + form_pfx + 'bwbps_uploadfile').val();
			break;
		
		case 1 :	//Image URL
			bFilled = $j('#' + form_pfx + 'bwbps_uploadurl').val();
			if(bFilled){
				$j('#' + form_pfx + 'bwbps_uploadfile').val("");
			}
			break;
		
		case 2 :	//Image Direct Link
			bFilled = $j('#' + form_pfx + 'bwbps_uploaddl').val();
			if(bFilled){
				$j('#' + form_pfx + 'bwbps_uploadfile').val("");
			}
			break;
			
		case 3 :	//YouTube URL
			bFilled = $j('#' + form_pfx + 'bwbps_uploadyt').val();
			break;
		
		case 4 :	//Image URL
			bFilled = $j('#' + form_pfx + 'bwbps_uploadvid').val();
			break;
		
		case 5 :	//Image for File 2
			bFilled = $j('#' + form_pfx + 'bwbps_uploadfile2').val();
			break;
			
		case 6 :	//Image URL for File 2
			bFilled = $j('#' + form_pfx + 'bwbps_uploadurl2').val();
			if(bFilled){
				$j('#' + form_pfx + 'bwbps_uploadfile2').val("");
			}
			break;
		
		default :
			bFilled = true;
			break;
	}
	
	if( bFilled ){ return true; } else { return false; }
}

// Callback for successful Ajax image upload
// Displays the image or error messages
function bwbpsUploadSuccess(data, statusText, form_pfx)  {
	//This Alternate function is set in PhotoSmash Settings Advanced page
	//If the alternate function returns false...continue with standard function
	
	$j("#" + form_pfx + "bwbps_loading").hide(); 
	
	if(bwbpsAlternateUploadFunction(data, statusText, form_pfx)){ return false;}

	$j('#' + form_pfx + 'bwbps_submitBtn').removeAttr('disabled');
	$j('#' + form_pfx + 'bwbps_imgcaptionInput').removeAttr('disabled');
	bwbpsUploadStatus = true;
	if (statusText == 'success') {
		if(data == -1){
				alert('security failure: nonce');
			//The nonce	 check failed
			$j('#' + form_pfx + 'bwbps_message').html("<span class='error'>Upload failed due to invalid authorization.  Please reload this page and try again.</span>");
			
			if(data.special_msg){
				$j('#' + form_pfx + 'bwbps_message').append("<p>" + unescape(data.special_msg) + "</p>");
			}
			
			return false;
	 	}
	 	
		if( data.succeed == 'false'){
			//Failed for some reason
			$j('#' + form_pfx + 'bwbps_message').html(data.message);
			if(data.special_msg){
				$j('#' + form_pfx + 'bwbps_message').append("<p id='bwbps_special_msg'>" + unescape(data.special_msg) + "</p>");
			}
			return false;
		}
		
		if (data.db_saved > 0 ) {
			//We got an image back...show it
			
			if( data.thumb_fullurl ){
				$j('#' + form_pfx + 'bwbps_result').html('<img src="' + data.thumb_fullurl +'" />'); 
			} else {
				$j('#' + form_pfx + 'bwbps_result').html('<img src="' + bwbpsThumbsURL + data.img+'" />'); 
			}
			
			
			if(data.message == undefined){	
				data.message = "";
			} else { 
				data.message = "<br/>" + data.message;
			}
			$j('#' + form_pfx + 'bwbps_message').html('<b>Upload successful!</b>' + data.message);
			
			
			
			//Reset form fields
			$j('.bwbps_reset').val('');
			
			//Add the New Images box for custom Layouts			
			var adderdiv;
			
			//If this is the first added image && it's a custom form, create the container
			if( $j('#bwbps_stdgal_' + data.gallery_id).length == 0 ){
				adderdiv = $j('<div></div>');
				adderdiv.attr('id','bwbps_galcont_' + data.gallery_id).attr('class','');
				
				$j('<h2></h2>').html('Added Images').appendTo(adderdiv);
				
				var bbtbl = $j('<table></table>');
				var bbtr = $j('<tr></tr>');
				bbtr.appendTo(bbtbl);
				var bbtd = $j('<td></td>');
				
				bbtd.appendTo(bbtr);
				
				var newImgUL = $j('<ul></ul>').attr('class','bwbps_gallery bwbps_custom_add')
					.attr('id','bwbps_stdgal_' + data.gallery_id);
				newImgUL.appendTo(bbtd);
				bbtbl.appendTo(adderdiv);
				adderdiv.insertAfter('#bwbpsInsertBox_' + data.gallery_id);
			}
			
			
			
			var li = $j('<li></li>').attr('class','ps_newimage psgal_' + data.gallery_id).appendTo('#bwbps_stdgal_' + data.gallery_id);
			
			if (data.li_width > 0) {
				li.css('width', data.li_width + '%');
			}else{
				li.css('margin','15px');	
			}	
			
			if(Number(data.thumb_height)){
				var thumb_ht = Number(data.thumb_height) + 15;
			}
			
			if(thumb_ht == 'NaN' || thumb_ht < 16){
				thumb_ht = 125;
			}
			
			//Manually set the LI height for Custom Layouts
			if($j('#bwbps_stdgal_' + data.gallery_id).hasClass('bwbps_custom_add')){
				li.css('height', thumb_ht + "px");
			}
			
			
			var imgdiv;
			
			if ($j.browser.msie) {
				imgdiv = $j('<div></div>').css('width', data.thumb_width);
			} else {
				imgdiv = $j('<div></div>').css('width', data.thumb_width).css('margin', 'auto');
			}
						
			var ahref;	// The image's link
			
			//Handle the new upload method
			if( data.thumb_fullurl ){
			
				ahref = $j('<a></a>').attr('href', data.image_fullurl).attr('rel',data.imgrel);
				$j('<img src="' + data.thumb_fullurl +'" />').appendTo(ahref);
				
			} else {
			
				ahref = $j('<a></a>').attr('href', bwbpsImagesURL + data.img).attr('rel',data.imgrel);
				$j('<img src="' + bwbpsThumbsURL + data.img +'" />').appendTo(ahref);
				
			}
			
			
					
			if(data.show_imgcaption > 0){
				$j('<br />').appendTo(ahref);
				$j('<span>' + data.image_caption + '</span>').attr('class','bwbps_caption').appendTo(ahref);
			}
			
			ahref.appendTo(imgdiv);
			
			imgdiv.appendTo(li);
			
			bwbps_equalHeight($j('.psgal_' + data.gallery_id));
			
			li.append('&nbsp;');

			if(data.special_msg){
				var pv = "";
				if(data.preview_id){ pv = "<a href='/?p=" + data.preview_id + "&preview=true' target='_blank'>Preview</a>";}
				$j('#' + form_pfx + 'bwbps_previewpost').append("<p>" + pv + unescape(data.special_msg) + "</p>");
			}
			
			
		} else {
			$j('#' + form_pfx + 'bwbps_message').html( "Image not saved: " + data.error); 
			if(data.special_msg){
				$j('#' + form_pfx + 'bwbps_message').append("<p>" + unescape(data.special_msg) + "</p>");
			}
		}
	} else {
		$j('#' + form_pfx + 'bwbps_message').html('Unknown error!'); 
	}
} 


//Show the Photo Upload Form
function bwbpsShowPhotoUpload(gal_id, post_id, form_pfx){
	if( form_pfx == null ){ form_pfx = ""; }
	bwbpsActiveGallery = gal_id;
	$j('#' + form_pfx + 'bwbps_galleryid').val(gal_id);
	$j('#' + form_pfx + 'bwbps_post_id').val(post_id);
	$j('#' + form_pfx + "bwbps_submitBtn").removeAttr('disabled');
}

function bwbpsShowPhotoUploadNoThickbox(gal_id, post_id, form_pfx){
	bwbpsActiveGallery = gal_id;
	if(!form_pfx){form_pfx = "";}
	$j('#' + form_pfx + 'bwbps_galleryid').val(gal_id);
	$j('#' + form_pfx + 'bwbps_post_id').val(post_id);
	$j('#' + form_pfx + 'bwbps-formcont').hide();
	$j('#' + form_pfx + 'bwbps-formcont').appendTo('#bwbpsFormSpace_' + gal_id);
	
	$j('#bwbpsFormSpace_' + gal_id).show();
	$j('#' + form_pfx + 'bwbps-formcont').show('slow');
	$j('#' + form_pfx + "bwbps_submitBtn").removeAttr('disabled');
}

function bwbpsHideUploadForm(gal_id, form_pfx){
	if(!gal_id){gal_id = "";}
	if( form_pfx == null ){ form_pfx = ""; }
	$j('#' + form_pfx + 'bwbps-formcont').hide('slow');
	$j('#bwbpsFormSpace_' + gal_id).hide('slow');
}

function bwbpsSwitchUploadField(field_id, select_iteration, form_pfx){
	if( select_iteration == null ){ select_iteration = ""; }
	if( form_pfx == null ){ form_pfx = ""; }
	$j( "." + form_pfx + "bwbps_uploadspans" + select_iteration ).hide();
	$j("#" + field_id).fadeIn("slow");
	
}

//Toggle Form Visible setting in PhotoSmash Default Settings Admin page
function bwbpsToggleFormAlwaysVisible(){
	if($j("#bwbps_use_thickbox").attr('checked')){
		$j("#bwbps_formviz").fadeOut('slow');
	} else {
		$j("#bwbps_formviz").fadeIn('slow');
	}
	
}

//Reset the height of all the LI's to be the same
function bwbps_equalHeight(group) {
    tallest = 0;
    group.each(function() {
        thisHeight = $j(this).height();
        if(thisHeight > tallest) {
            tallest = thisHeight;
        }
    });
    group.height(tallest);
}


function bwbpsToggleDivHeight(ele_id, tog_ht){
	if($j("#" + ele_id).css('height') == tog_ht){
		$j("#" + ele_id).css('height', 'auto');
		
	} else {
		$j("#" + ele_id).css('height', tog_ht);
	}
}

function bwbpsToggleCheckboxes(chkbox_class, chk_val){
	$j('input:checkbox.' + chkbox_class ).attr('checked', chk_val);
}


//Moderate/Delete Image
function bwbpsModerateImage(action, image_id, post_id)
{
	var imgid = parseInt('' + image_id);
	var myaction = false;
	var actiontext = "";
	
	var postid = parseInt('' + post_id); 
	
	
	var img_id_text = " (image id: " + imgid + ")";
	
	var sendMsg = jQuery("#ps_mod_send_msg").attr('checked') ? 1 : 0;
	var modMsg = '';
	var confirmOn = true;
	
	switch (action) {
		case 'publishpost' :
			myaction = action;
			actiontext = "publish post: " + postid;
			break;
			
		case 'remove' :
			myaction = action;
			actiontext = "remove this image from gallery (leaves Media Library images in tact) ";
			break;
		
		case 'bury' :
			myaction = 'delete';
			actiontext = "delete this image (Note: will only 'remove' if more than one record uses image. Will delete images from Media Gallery if 'delete' is used. Use 'remove' to leave Media Gallery images in tact.) ";
			
			if( sendMsg && !confirm('Is Rejection Moderation Message correct?\n\n ' + jQuery("#ps_mod_reject_msg").val() )){
				return;
			} else {
				if( sendMsg ){
					modMsg = jQuery("#ps_mod_reject_msg").val();
				}
			}
			
			break;
			
		case 'approve' :
			myaction = action;
			actiontext = "approve this image ";
			
			
			if( sendMsg && !confirm('Is Approve Moderation Message correct?\n\n ' + jQuery("#ps_mod_approve_msg").val() )){
				return;
			} else {
				if( sendMsg ){
					modMsg = jQuery("#ps_mod_approve_msg").val();
				}
			}
			
			break;
		
		case 'review' :
			myaction = action;
			actiontext = "mark this image as reviewed ";
			break;
			
		case 'savecaption' :
			myaction = action;
			actiontext = "save image data ";
			break;
			
		case 'saveall' :
			myaction = 'savecaption';
			actiontext = "save image data ";
			confirmOn = false;
			break;		
	
	}
		
	if(!myaction){ alert('Invalid action.'); return false;}
	
	if(confirmOn){
		if(!confirm('Do you want to ' + actiontext + img_id_text + ')?')){ return false;}
	}
	
	var _moderate_nonce = $j("#_moderate_nonce").val();
	
	var image_caption = '';
	var image_url = "";
	var image_tags = "";
	var meta_data = "";
	var file_url = "";
	var image_seq = "";
	var image_geolat = 0;
	var image_geolong = 0;
	var image_post_id = 0;
	if(myaction == 'savecaption'){ 
		image_caption = $j('#imgcaption_' + imgid).val(); 
		image_url = $j('#imgurl_' + imgid).val();
		image_seq = $j('#imgseq_' + imgid).val();
		image_tags = $j('#imgtags_' + imgid).val(); 
		image_geolat = $j('#geolat_' + imgid).val();
		image_geolong = $j('#geolong_' + imgid).val();
		meta_data = $j('#imgmeta_' + imgid).val(); 
		file_url = $j('#fileurl_' + imgid).val(); 
		image_post_id  = $j('#image_post_id_' + imgid).val(); 
	}
	
	try{
		$j('#ps_savemsg').show();
	}catch(err){}
	
	$j.ajax({
		type: 'POST',
		url: bwbpsAjaxURL,
		data: { 'action': myaction,
       'image_id': imgid,
       '_ajax_nonce' : _moderate_nonce,
       'image_caption' : image_caption,
       'image_url' : image_url,
       'image_geolat' : image_geolat,
       'image_geolong' : image_geolong,
       'image_tags' : image_tags,
       'meta_data' : meta_data,
	   'file_url' : file_url,
	   'seq' : image_seq,
       'post_id' : postid,
       'image_post_id' : image_post_id,
	   'mod_msg' : modMsg,
	   'send_msg' : sendMsg
       },
		dataType: 'json',
		success: function(data) {
			bwbpsModerateSuccess(data, imgid);
		}
	});
	return false;
}

// Callback for successful Ajax image moderation
function bwbpsModerateSuccess(data, imgid)  { 

		jQuery('.bwbps_save_flds_' + imgid).attr('src',bwbpsPhotoSmashURL + 'images/disk.png');
		
		try{
			$j('#ps_savemsg').hide();
		}catch(err){}
		if(data == -1){
				alert('Failed due to security: invalid nonce');
			//The nonce	 check failed
			$j('#psmod_' + imgid).html("fail: security"); 
			return false;
	 	}
	 	
		if( data.status == 'false' || data.status == 0){
			//Failed for some reason
			$j('#psmod_' + imgid).html("update: fail"); 
			return false;
		} else {
			
			switch (data.action) {
			
				case 'published' :
					$j('#psimg_pubpost' + imgid ).html('published');
					$j('#psimg_pubpost' + imgid ).addClass('ps-moderate');			
					break;
					
				default :
					//this one passed
					$j('#psmod_' + imgid).html(''); 
					$j('#psmodmsg_' + imgid).html( data.action); 
					if(data.deleted == 'deleted'){
						$j('#psimage_' + imgid).html('');				
					}
					
					$j('#psimg_' + imgid).removeClass('ps-moderate');
				
					break;
			}
		
			return false;
		}
}



/**
 *  jQuery Plugin highlightFade (jquery.offput.ca/highlightFade)
 *  (c) 2006 Blair Mitchelmore (offput.ca) blair@offput.ca
 */
/**
 * This is version 0.7 of my highlightFade plugin. It follows the yellow fade technique of Web 2.0 fame
 * but expands it to allow any starting colour and allows you to specify the end colour as well.
 *
 * For the moment, I'm done with this plug-in. Unless I come upon a really cool feature it should have
 * this plug-in will only receive updates to ensure future compatibility with jQuery.
 *
 * As of now (Aug. 16, 2006) the plugin has been written with the 1.0.1 release of jQuery (rev 249) which
 * is available from http://jquery.com/src/jquery-1.0.1.js
 *
 * A note regarding rgb() syntax: I noticed that most browsers implement rgb syntax as either an integer 
 * (0-255) or percentage (0-100%) value for each field, that is, rgb(i/p,i/p,i/p); however, the W3C 
 * standard clearly defines it as "either three integer values or three percentage values" [http://www.w3.org/TR/CSS21/syndata.html] 
 * which I choose to follow despite the error redundancy of the typical behaviour browsers employ.
 *
 * Changelog:
 *
 *    0.7:
 *        - Added the awesome custom attribute support written by George Adamson (slightly modified)
 *        - Removed bgColor plugin dependency seeing as attr is customizable now...
 *    0.6:
 *        - Abstracted getBGColor into its own plugin with optional test and data retrieval functions
 *        - Converted all $ references to jQuery references as John's code seems to be shifting away
 *          from that and I don't want to have to update this for a long time.
 *    0.5:
 *        - Added simple argument syntax for only specifying start colour of event
 *        - Removed old style argument syntax
 *        - Added 'interval', 'final, and 'end' properties
 *        - Renamed 'color' property to 'start'
 *        - Added second argument to $.highlightFade.getBGColor to bypass the e.highlighting check
 *    0.4:
 *        - Added rgb(%,%,%) color syntax
 *    0.3:
 *        - Fixed bug when event was called while parent was also running event corrupting the
 *          the background colour of the child
 *    0.2:
 *        - Fixed bug where an unspecified onComplete function made the page throw continuous errors
 *        - Fixed bug where multiple events on the same element would speed each subsequent event
 *    0.1:
 *        - Initial Release
 * 
 * @author          Blair Mitchelmore (blair@offput.ca)
 * @version         0.5
 
 
 *  PhotoSmash note...I've renamed functions to bwbpsFade to avoid namespace clashes
 
 */
jQuery.fn.bwbpsFade = function(settings) {
	var o = (settings && settings.constructor == String) ? {start: settings} : settings || {};
	var d = jQuery.bwbpsFade.defaults;
	var i = o['interval'] || d['interval'];
	var a = o['attr'] || d['attr'];
	var ts = {
		'linear': function(s,e,t,c) { return parseInt(s+(c/t)*(e-s)); },
		'sinusoidal': function(s,e,t,c) { return parseInt(s+Math.sin(((c/t)*90)*(Math.PI/180))*(e-s)); },
		'exponential': function(s,e,t,c) { return parseInt(s+(Math.pow(c/t,2))*(e-s)); }
	};
	var t = (o['iterator'] && o['iterator'].constructor == Function) ? o['iterator'] : ts[o['iterator']] || ts[d['iterator']] || ts['linear'];
	if (d['iterator'] && d['iterator'].constructor == Function) t = d['iterator'];
	return this.each(function() {
		if (!this.highlighting) this.highlighting = {};
		var e = (this.highlighting[a]) ? this.highlighting[a].end : jQuery.bwbpsFade.getBaseValue(this,a) || [255,255,255];
		var c = jQuery.bwbpsFade.getRGB(o['start'] || o['colour'] || o['color'] || d['start'] || [255,255,128]);
		var s = jQuery.speed(o['speed'] || d['speed']);
		var r = o['final'] || (this.highlighting[a] && this.highlighting[a].orig) ? this.highlighting[a].orig : jQuery.curCSS(this,a);
		if (o['end'] || d['end']) r = jQuery.bwbpsFade.asRGBString(e = jQuery.bwbpsFade.getRGB(o['end'] || d['end']));
		if (typeof o['final'] != 'undefined') r = o['final'];
		if (this.highlighting[a] && this.highlighting[a].timer) window.clearInterval(this.highlighting[a].timer);
		this.highlighting[a] = { steps: ((s.duration) / i), interval: i, currentStep: 0, start: c, end: e, orig: r, attr: a };
		jQuery.bwbpsFade(this,a,o['complete'],t);
	});
};

jQuery.bwbpsFade = function(e,a,o,t) {
	e.highlighting[a].timer = window.setInterval(function() { 
		var newR = t(e.highlighting[a].start[0],e.highlighting[a].end[0],e.highlighting[a].steps,e.highlighting[a].currentStep);
		var newG = t(e.highlighting[a].start[1],e.highlighting[a].end[1],e.highlighting[a].steps,e.highlighting[a].currentStep);
		var newB = t(e.highlighting[a].start[2],e.highlighting[a].end[2],e.highlighting[a].steps,e.highlighting[a].currentStep);
		jQuery(e).css(a,jQuery.bwbpsFade.asRGBString([newR,newG,newB]));
		if (e.highlighting[a].currentStep++ >= e.highlighting[a].steps) {
			jQuery(e).css(a,e.highlighting[a].orig || '');
			window.clearInterval(e.highlighting[a].timer);
			e.highlighting[a] = null;
			if (o && o.constructor == Function) o.call(e);
		}
	},e.highlighting[a].interval);
};

jQuery.bwbpsFade.defaults = {
	start: [255,255,128],
	interval: 50,
	speed: 400,
	attr: 'backgroundColor'
};

jQuery.bwbpsFade.getRGB = function(c,d) {
	var result;
	if (c && c.constructor == Array && c.length == 3) return c;
	if (result = /rgb\(\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*,\s*([0-9]{1,3})\s*\)/.exec(c))
		return [parseInt(result[1]),parseInt(result[2]),parseInt(result[3])];
	else if (result = /rgb\(\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*,\s*([0-9]+(?:\.[0-9]+)?)\%\s*\)/.exec(c))
		return [parseFloat(result[1])*2.55,parseFloat(result[2])*2.55,parseFloat(result[3])*2.55];
	else if (result = /#([a-fA-F0-9]{2})([a-fA-F0-9]{2})([a-fA-F0-9]{2})/.exec(c))
		return [parseInt("0x" + result[1]),parseInt("0x" + result[2]),parseInt("0x" + result[3])];
	else if (result = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])/.exec(c))
		return [parseInt("0x"+ result[1] + result[1]),parseInt("0x" + result[2] + result[2]),parseInt("0x" + result[3] + result[3])];
	else
		return jQuery.bwbpsFade.checkColorName(c) || d || null;
};

jQuery.bwbpsFade.asRGBString = function(a) {
	return "rgb(" + a.join(",") + ")";
};

jQuery.bwbpsFade.getBaseValue = function(e,a,b) {
	var s, t;
	b = b || false;
	t = a = a || jQuery.bwbpsFade.defaults['attr'];
	do {
		s = jQuery(e).css(t || 'backgroundColor');
		if ((s  != '' && s != 'transparent') || (e.tagName.toLowerCase() == "body") || (!b && e.highlighting && e.highlighting[a] && e.highlighting[a].end)) break; 
		t = false;
	} while (e = e.parentNode);
	if (!b && e.highlighting && e.highlighting[a] && e.highlighting[a].end) s = e.highlighting[a].end;
	if (s == undefined || s == '' || s == 'transparent') s = [255,255,255];
	return jQuery.bwbpsFade.getRGB(s);
};

jQuery.bwbpsFade.checkColorName = function(c) {
	if (!c) return null;
	switch(c.replace(/^\s*|\s*$/g,'').toLowerCase()) {
		case 'aqua': return [0,255,255];
		case 'black': return [0,0,0];
		case 'blue': return [0,0,255];
		case 'fuchsia': return [255,0,255];
		case 'gray': return [128,128,128];
		case 'green': return [0,128,0];
		case 'lime': return [0,255,0];
		case 'maroon': return [128,0,0];
		case 'navy': return [0,0,128];
		case 'olive': return [128,128,0];
		case 'purple': return [128,0,128];
		case 'red': return [255,0,0];
		case 'silver': return [192,192,192];
		case 'teal': return [0,128,128];
		case 'white': return [255,255,255];
		case 'yellow': return [255,255,0];
	}
};