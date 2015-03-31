//
//  Autogenerated by CocoaTouchApiGenerator
//
//  DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
//



#import "EsMessage.h"
#import "EsMessageType.h"
#import "EsRequest.h"
#import "EsResponse.h"
#import "EsEvent.h"
#import "EsEntity.h"
#import "EsObject.h"
#import "ThriftGetUserCountRequest.h"

/**
 * This request asks the server for the current number of users logged in. It leads to a a GetUserCountResponse which contains the user count.
 * 
 * This shows how to request the concurrent logged in user count from the server, capture the response, and print the result.
<pre>
private var _es:ElectroServer;

private function initialize():void {
        _es.engine.addEventListener(MessageType.GetUserCountResponse.name, onGetUserCountResponse);

        var gucr:GetUserCountRequest = new GetUserCountRequest();
        _es.engine.send(gucr);
}

private function onGetUserCountResponse(e:GetUserCountResponse):void {
        trace("Number of users logged in: " + e.count.toString());
}
</pre>
 */
@interface EsGetUserCountRequest : EsRequest {
@private
}


- (id) init;
- (id) initWithThriftObject: (id) thriftObject;
- (void) fromThrift: (id) thriftObject;
- (ThriftGetUserCountRequest*) toThrift;
- (id) newThrift;
@end