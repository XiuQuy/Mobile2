SET IDENTITY_INSERT [dbo].[WatchList] ON 

INSERT [dbo].[WatchList] ([Id], [Title], [UserId], [ItemCount]) VALUES (1, N'Playlist1', 1, 25)
INSERT [dbo].[WatchList] ([Id], [Title], [UserId], [ItemCount]) VALUES (2, N'Playlist2', 1, 4)
INSERT [dbo].[WatchList] ([Id], [Title], [UserId], [ItemCount]) VALUES (3, N'Playlist 3', 1, 5)
INSERT [dbo].[WatchList] ([Id], [Title], [UserId], [ItemCount]) VALUES (4, N'Playlist 4', 1, 3)
INSERT [dbo].[WatchList] ([Id], [Title], [UserId], [ItemCount]) VALUES (5, N'Playlist 5', 1, 0)

SET IDENTITY_INSERT [dbo].[WatchList] OFF
GO
