var j = jQuery;
j(document).ready( function() {
	
	// ajax code to quick show tag group results
	j('#groups-directory-form .gtags a').click( function(event) {
		tag = j(event.target).text();
		j(event.target).addClass('loading');
		j('input#groups_search').val('').focus().blur();
		
		j.post( ajaxurl, { action: 'gtags', 'tag': tag },
			function(response) {
				j('#groups-dir-list').fadeOut( 100, function() {
					j('#groups-dir-list').html(response);
					j('div.item-list-tabs li').each( function() { j(this).removeClass('selected'); });
					j('div.item-list-tabs li#groups-all').addClass('selected');
					j('#groups-dir-list').fadeIn(100);
			 	});
				j(event.target).removeClass('loading');
			});		
		
		return false;
	});
	
	//code for tag chooser
	j('.gtags-add').click(function (){
		contents = j('#group-tags').val();
		if ( contents != '' ) { sep = ', '; } else { sep = ''; }
		tag = j(event.target).text();
		j('#group-tags').val( contents + sep + tag );
	});		
	
	j('#gtags-toggle-top').click(function() {
		j('#gtags-top-cloud').show();
		j('#gtags-toggle-top').hide();
	})

});
