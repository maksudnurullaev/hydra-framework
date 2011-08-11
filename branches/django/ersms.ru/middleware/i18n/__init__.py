from django.conf import settings

from django.utils.cache import patch_vary_headers
from django.utils import translation

class Middleware(object):
    """
    This Middleware saves the desired content language in the user session.
    The SessionMiddleware has to be activated.
    """
    @staticmethod
    def process_request(request):
        if request.method == 'GET' and 'lang' in request.GET:
                language = request.GET['lang']
                request.session['language'] = language
        elif 'language' in request.session:
                language = request.session['language']
        else:
                language = translation.get_language_from_request(request)
        
        for lang in settings.LANGUAGES:
            if lang[0] == language:
                translation.activate(language)
                
        request.LANGUAGE_CODE = translation.get_language()
        return None

    @staticmethod
    def process_response(request, response):
        patch_vary_headers(response, ('Accept-Language',))
        if 'Content-Language' not in response:
            response['Content-Language'] = translation.get_language()
        translation.deactivate()
        return response 

