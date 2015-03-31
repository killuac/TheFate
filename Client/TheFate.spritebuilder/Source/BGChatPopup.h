//
//  BGChatPopup.h
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

#if __CC_PLATFORM_IOS
#import "BGTableView.h"

@interface BGChatPopup : BGPopup <UITableViewDataSource, UITableViewDelegate> {
    BGTableView *_tableView;
#else
@interface BGChatPopup : BGPopup {
#endif
    CCTextField *_messageText;
    CCLayoutBox *_buttonBox;
}

- (void)segmentSelected:(CCButton *)sender;

- (void)reloadTableViewData;

+ (void)initializeHistory;
+ (void)addHistory:(NSString *)message byUser:(NSString *)nickName;
+ (void)clearHistory;

@end
