// JavaScript Document
/*************************************************
Star Rating System
First Version: 21 November, 2006
Second Version: 17 May, 2007
Author: Ritesh Agrawal (http://php.scripts.psu.edu/rja171/widgets/rating.php)
Inspiration: Will Stuckey's star rating system (http://sandbox.wilstuckey.com/jquery-ratings/)
Half-Star Addition: Karl Swedberg
Demonstration: http://examples.learningjquery.com/rating/
Usage: $j('#rating').rating('url-to-post.php', {maxvalue:5, curvalue:0});

arguments
url : required -- post changes to 
options
  increment : 1, // value to increment by
	maxvalue: number of stars
	curvalue: number of selected stars
	

************************************************/

jQuery.fn.psvoting = function(url, options) {
	if(url == null) {
		return;
	}
		
	url = bwbpsAjaxRateImage + "?action=voteimage&" + url;

	var settings = {
	url : url, // post changes to 
	increment : 1, // value to increment by
	maxvalue  : 5,   // max number of stars
	curvalue  : 0,    // number of selected stars
	uponly : 0,		// only show up voting link
	rating_cnt : 0
	};

	if(options) {
	jQuery.extend(settings, options);
	};

	var container = jQuery(this);

	var startitle = "";
	jQuery.extend(container, {
	url: settings.url
	});

	var innerbox = jQuery('<div></div>');
	
	container.empty().append(innerbox);
	
	var votetotal;
	var votinglinks;
	
	var voteinfo;
	
	//If the Postion is Top-Right (value == 0)
	if(!settings.rating_position){
		innerbox.addClass("bwbps-vote-data");
	
		var allowrate = settings.allow_rating ? 'bwbps-star' : 'bwbps-star bwbps-norate';
	
		
	
		votetotal = jQuery('<div class="bwbps-vote-total">' + settings.curvalue + '</div>');
		voteinfo = jQuery('<div class="bwbps-vote-info" style="display:none;"># votes: ' + settings.rating_cnt + '</div>');

	
		innerbox.append(votetotal);
		
		votinglinks = jQuery('<div class="bwbps-vote-links"></div>');
		innerbox.append(votinglinks);
			
	} else {
		
		//For Position beneath caption - place Voting Links first, then Vote count
		votetotal = jQuery('<span class="bwbps-vote-total">[' + settings.curvalue + ']</span>');
		
		voteinfo = jQuery('<div class="bwbps-vote-info"># votes: ' + settings.rating_cnt + '</div>');

		
		votinglinks = jQuery('<span></span>');
		
		innerbox.append(votinglinks);
		innerbox.append(votetotal);
		
	}
	
	innerbox.append(voteinfo);
	
	var voteupbutton;
	if(!settings.uponly){ 
		voteupbutton = '<img src="' + bwbpsPhotoSmashURL + 'images/thumb_up.png" alt="vote up" />';
	} else {
		voteupbutton = 'Vote';
	}
	
	var upvote = jQuery('<a href="#up" title="Vote up"></a>').html(voteupbutton);
	
	

	//Add the Up Vote
	upvote.click(function(){
		votetotal.empty();
		votetotal.addClass("bwbps-vote-small");
		votetotal.html('saving...');
			
		jQuery.post(container.url, {
			"rating": 1 
		}, function(data){ 
			votetotal.addClass("bwbps-vote-small");
			votetotal.empty().html(data);
		});
		
		return false;
		
	});
	
	votinglinks.append(upvote);
	
	//Add the Down Vote if uponly is false
	if(!settings.uponly){
		
		votinglinks.append("&nbsp;");
		
		var downvote = jQuery('<a href="#up" title="Vote down"><img src="' + bwbpsPhotoSmashURL + 'images/thumb_down.png" alt="vote down" /></a>');

		//Add the Up Vote
		downvote.click(function(){
			
			votetotal.empty();
			votetotal.addClass("bwbps-vote-small");
			votetotal.html('saving...');
				
			jQuery.post(container.url, {
				"rating": -1 
			}, function(data){ 
				votetotal.addClass("bwbps-vote-small");
				votetotal.empty().html(data);
			});
			
			return false;
			
		});
		
		votinglinks.append(downvote);	
	}
  

	
	//If the Postion is Top-Right (value == 0)
	if(!settings.rating_position){
	//Add mouseover to display info box (top-right position only);
		container
		.mouseover(function(){
			container.addClass('bwbps-voting-hover').end();
			container.removeClass('bwbps-voting-bkg').end();
			voteinfo.show();
		})
		.mouseout(function(){
			container.addClass('bwbps-voting-bkg').end();
			container.removeClass('bwbps-voting-hover').end();
			voteinfo.hide();
		});
	}
	    	
}


/*
 *  STAR RATING
 *
 */
jQuery.fn.psrating = function(url, options) {
	
	if(url == null) return;
	url = bwbpsAjaxRateImage + "?action=rateimage&" + url;
	var settings = {
    url : url, // post changes to 
    increment : 1, // value to increment by
    curvalue  : 0    // number of selected stars
  };
	
  if(options) {
    jQuery.extend(settings, options);
  };
  jQuery.extend(settings, {cancel: (settings.maxvalue > 1) ? true : false});
   
   
  var container = jQuery(this);
  var startitle = "";
	
	jQuery.extend(container, {
    averageRating: settings.curvalue,
    url: settings.url
  });
  settings.increment = (settings.increment < .75) ? .5 : 1;
  
  var innerbox = $j('<div class="bwbps-rating"></div>');
  
  var allowrate = settings.allow_rating ? 'bwbps-star' : 'bwbps-star bwbps-norate';
  container.empty().append(innerbox);
  var s = 0;
	for(var i= 0; i <= settings.maxvalue ; i++){
    if (i == 0) {
	    if(settings.cancel == true){
	    //Switch these to show cancel
        //var div = '<div class="cancel"><a href="#0" title="Cancel Rating">Cancel Rating</a></div>';
        var div = '';
        innerbox.empty().append(div);
      }
    } else {
      
      startitle = settings.allow_rating ? "Give it " + i + "/" + settings.maxvalue : "Login required to rate.";
      
      var $div = $j('<div class="' + allowrate + '"></div>')
        .append('<a href="#'+i+'" title="'+ startitle +'">'+i+'</a>')
        .appendTo(innerbox);
      if (settings.increment == .5) {
        if (s%2) {
          $div.addClass('bwbps-star-left');
        } else {
          $div.addClass('bwbps-star-right');
        }
      }
      
    }
    i=i-1+settings.increment;
    s++;
  }
  
  //If the Postion is Top-Right (value == 0)
  if(!settings.rating_position){
  
  	innerbox.addClass(' bwbps-rating-container');	//Class makes it nice and big
  	
  	var infobox = $j('<div class="bwbps-rating-infobox">' + settings.avg_rating + ' (' + settings.rating_cnt + ' votes)</div>').appendTo(innerbox).css('display', 'none').addClass('bwbps-rating-info');
  	
  	var infoclear = $j('<a></a>').click(function(){
  		container.css('display','none');
  		}
  	).appendTo(infobox).attr('title','hide rating');
  
  	
  	infoclear.append($j('<img>').attr('src',bwbpsPhotoSmashURL + "css/images/cancel-12.png").addClass('bwbps-rating-clearimg'));
  	
  	//Add mouseover to display info box (top-right position only);
  	innerbox
  	.mouseover(function(){
  		innerbox.addClass('bwbps-rating-hover').end();
  		infobox.show();
  	})
	.mouseout(function(){
		
		var relatedTarget =  event.relatedTarget ? event.relatedTarget : event.toElement;		
		innerbox.removeClass('bwbps-rating-hover').end();
		infobox.hide();
		
  	})
  	;
  	
  } else {
  
  //Position is beneath capiton
  	var infobox = $j('<p>' + settings.avg_rating + ' (' + settings.rating_cnt + ' votes)</p>')
  		.css('clear', 'both')
  		.appendTo(innerbox);
  
  }
	
  var stars = jQuery(innerbox).children('.bwbps-star');
  var cancel = jQuery(innerbox).children('.cancel');
  
  
  if(settings.allow_rating){
	  stars
	    .mouseover(function(){
	      event.drain();
	      event.fill(this);
	    })
	    .mouseout(function(){
	      event.drain();
	      event.reset();
	    })
	    .focus(function(){
	      event.drain();
	      event.fill(this);
	    })
	    .blur(function(){
	      event.drain();
	      event.reset();
	    });
	
	  stars.click(function(){
			if(settings.cancel == true){
	      settings.curvalue = (stars.index(this) * settings.increment) + settings.increment;
	      jQuery.post(container.url, {
	        "rating": jQuery(this).children('a')[0].href.split('#')[1] 
	      
	      }, function(data){ 
	      		infobox.empty().html(data).append(infoclear);
	      	}
	      );
				return false;
			} else if (settings.maxvalue == 1) {
				settings.curvalue = (settings.curvalue == 0) ? 1 : 0;
				$j(this).toggleClass('bwbps-on');
				jQuery.post(container.url, {
	        "rating": jQuery(this).children('a')[0].href.split('#')[1] 
	      });
				return false;
			}
			return true;
				
	  });
  
  } else {
  	stars.click(function(){
  		alert('Login is required to rate.');
  	});
  
  }

  // cancel button events
	if(cancel){
    cancel
    .mouseover(function(){
      event.drain();
      jQuery(this).addClass('on');
    })
    .mouseout(function(){
      event.reset();
      jQuery(this).removeClass('on');
    })
    .focus(function(){
      event.drain();
      jQuery(this).addClass('on');
    })
    .blur(function(){
      event.reset();
      jQuery(this).removeClass('on');
    });
      
    // click events.
    cancel.click(function(){
      event.drain();
      settings.curvalue = 0;
      jQuery.post(container.url, {
        "rating": jQuery(this).children('a')[0].href.split('#')[1] 
      });
      return false;
    });
  }
        
	var event = {
		fill: function(el){ // fill to the current mouse position.
			var index = stars.index(el) + 1;
			stars
				.children('a').css('width', '100%').end()
				.slice(0,index).addClass('bwbps-hover').end();
		},
		drain: function() { // drain all the stars.
			stars
				.filter('.bwbps-on').removeClass('bwbps-on').end()
				.filter('.bwbps-hover').removeClass('bwbps-hover').end();
		},
		reset: function(){ // Reset the stars to the default index.
			stars.slice(0,settings.curvalue / settings.increment).addClass('bwbps-on').end();
		}
	};    
	event.reset();
	
	return(this);	

};

function bwbpsToggleRatings(gal_id){
	if(gal_id){
		$j('.bwbps-rating-gal-' + gal_id).toggle();
	}
}