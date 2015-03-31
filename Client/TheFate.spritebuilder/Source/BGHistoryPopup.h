//
//  BGHistoryPopup.h
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGPopup.h"

#if __CC_PLATFORM_IOS
#import "BGTableView.h"

@interface BGHistoryPopup : BGPopup <UITableViewDataSource, UITableViewDelegate> {
    BGTableView *_tableView;
#else
@interface BGHistoryPopup : BGPopup {
#endif
}

- (void)showWithDuration:(NSTimeInterval)duration;
- (void)dismissWithDuration:(NSTimeInterval)duration;

- (void)reloadHistoryData;

@end
