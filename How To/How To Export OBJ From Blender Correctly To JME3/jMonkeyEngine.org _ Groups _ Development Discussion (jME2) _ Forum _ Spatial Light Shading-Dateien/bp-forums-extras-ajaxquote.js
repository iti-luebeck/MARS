jQuery(document).ready( function() {
	
	jQuery('.ajaxquote').live('click', function() {
		
		jQuery(this).removeClass('loaded');
		
		var type = jQuery(this).attr('class');
		var id = jQuery(this).attr('id');
		
		var postid = id.substring(15,id.length);
		
		jQuery(this).addClass('loading');
		
		var nonce_input = jQuery("#"+postid+"_post_nonce");
		var nonce = nonce_input.val();
		
		var link_input = jQuery("#"+postid+"_post_url");
		var postlink = link_input.val();
		
		jQuery.post( ajaxurl, {
			action: 'bpforums_ajaxquote',
			'_wpnonce' : nonce,
			'postlink' : postlink,
			'cookie': encodeURIComponent(document.cookie),
			'type': type,
			'id': postid
		},
		function(data) {

			jQuery('#' + id).fadeOut( 100, function() {
				jQuery(this).html('Quoted').removeClass('loading').addClass('loaded').fadeIn(100);

			});


			previous_content = jQuery("textarea#reply_text").val();
			jQuery("textarea#reply_text").val( previous_content + data );

		});
		
		return false;
	});

});