<div class="body">
    <link rel="stylesheet" href="./resources/css/stylewordpress.min.css">
    <link rel="stylesheet" href="./resources/css/buttons.min.css">
    <h1 id="logo"><a href="http://wp-quick-install.com">WordPress</a></h1>

    <div id="response"></div>
    <div class="progress" style="display:none;">
        <div class="progress-bar progress-bar-striped active" style="width: 0%;"></div>
    </div>
    <div id="errors" class="alert alert-danger" style="display:none;">
        <strong>Warning</strong>
    </div>

    <div id="success" class="hidden" style="margin: 10px 0px;">
        <h1 style="margin: 0">The world is yours</h1>
        <p>WordPress has been installed.</p>
        <div id="errors" class="alert alert-danger"><p style="margin:0;">
                <strong>Warning</strong>: You created WordPress: {{wordpressName}} in Workspace {{wordpressName}}.</p>
        </div>
        <a href="{{urlAdmin}}" class="button wp-core-ui" style="margin-right:5px;" target="_blank">Log In</a>
        <a href="{{urlWebsite}}" class="button wp-core-ui" target="_blank">Go to website</a>
    </div>
    <div id="idsubmit">
        <form id="formsubmit" method="post" action="engine/default/worksapce/deployment/wordpress" ng-submit="submitDeployWordpress()">
            <h1 style="text-align: center">Workspace Name : {{workspace.workspaceName}}</h1>
            <p> </p>
            <table class="form-table " >
                <tbody>
                    <tr>
                        <th scope="row">
                            <label for="plugins">Install Default</label>
                        </th>
                        <td>
                            <label>
                                <input type="checkbox" id="checkboxDefault"  value="1" name="installDefault">
                                Host And Database
                            </label>
                        </td>
                    </tr>
                </tbody>
            </table>
            <table class="form-table hidden" id="formDefault">
                <tbody>
                    <tr>
                        <th scope="row"><label for="wphost">Wordpress Host</label></th>
                        <td><input name="wphost" id="wphost" type="text" size="25" value="{{wphost}}" class="required"></td>
                        <td>The name of the host wordpress  you want to run WP in.</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="wpinstall">Folder Install</label></th>
                        <td><input name="wpinstall" id="wpfolderinstall" type="text" size="25" value="{{wpinstall}}" class="required"></td>
                        <td>The folder install  of the wordpress  you want to run WP in.</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="dbname">Database name</label></th>
                        <td><input name="dbname" id="dbname" type="text" size="25" value="{{dbname}}" class="required"></td>
                        <td>The name of the database you want to run WP in.</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="uname">Database username</label></th>
                        <td><input name="uname" id="uname" type="text" size="25" value="root" class="required"></td>
                        <td>Your MySQL username</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="pwd">Password</label></th>
                        <td><input name="pwd" id="pwd" type="text" size="25" value=""></td>
                        <td>?and your MySQL password.</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="dbhost">Database Host</label></th>
                        <td><input name="dbhost" id="dbhost" type="text" size="25" value="localhost:3305" class="required"></td>
                        <td>You should be able to get this info from your web host, if <code>localhost</code> does not work.</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="prefix">Table Prefix</label></th>
                        <td><input name="prefix" id="prefix" type="text" value="wp_" size="25" class="required"></td>
                        <td>If you want to run multiple WordPress installations in a single database, change this.</td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="default_content">Default content</label></th>
                        <td>
                            <label><input type="checkbox" name="default_content" id="default_content" value="1" checked="checked"> Delete the content</label>
                        </td>
                        <td>If you want to delete the default content added par WordPress (post, page, comment and links).</td>
                    </tr>
                </tbody>
            </table>
            <h1>Install Wordpress Information</h1>
            <table class="form-table">
                <tbody>
                    <tr>
                        <th scope="row"><label for="language">Language</label></th>
                        <td>
                            <select id="language" name="language">
                                <option value="vi" selected>VietNam</option>
                                <option value="en_US">English (United States)</option>
                            </select>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="wordpressName">WordpressName</label>
                        </th>
                        <td>
                            <!--<input name="directory" type="text" id="directory" size="25" value="{{workspaceName}}">-->
                            <input name="wordpressName" type="text" id="wordpressName" size="25" value="{{wordpressName}}">
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="weblog_title">Site Title</label></th>
                        <td><input name="weblog_title" type="text" id="weblog_title" size="25" value="{{workspace.workspaceName}}" class="required"></td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="user_login">Username</label></th>
                        <td>
                            <input name="user_login" type="text" id="user_login" size="25" value="taonuaa004" class="required">
                            <p>Usernames can have only alphanumeric characters, spaces, underscores, hyphens, periods and the @ symbol.</p>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="admin_password">Password</label>
                            <p>A password will be automatically generated for you if you leave this blank.</p>
                        </th>
                        <td>
                            <input name="admin_password" type="password" id="admin_password" size="25" value="12121993a">
                            <p>Hint: The password should be at least seven characters long. To make it stronger, use upper and lower case letters, numbers and symbols like ! " ? $ % ^ &amp; )..</p>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="admin_email">Your E-mail</label></th>
                        <td><input name="admin_email" type="text" id="admin_email" size="25" value="taonuaa004@gmail.com" class="required">
                            <p>Double-check your email address before continuing.</p></td>
                    </tr>
                    <tr>
                        <th scope="row"><label for="blog_public">Privacy</label></th>
                        <td colspan="2"><label><input type="checkbox" id="blog_public" name="blog_public" value="1" checked="checked"> Allow search engines to index this site.</label></td>
                    </tr>
                </tbody></table>

            <h1>Theme Informations</h1>
            <p>Enter the information below for your personal theme.</p>
            <div class="alert alert-info">
                <p style="margin:0px; padding:0px;">WP Quick Install will automatically install your theme if it's on wp-quick-install folder and named theme.zip</p>
            </div>
            <table class="form-table">
                <tbody><tr>
                        <th scope="row">
                            <label for="activate_theme">Automatic Activation</label>
                        </th>
                        <td colspan="2">
                            <label><input type="checkbox" id="activate_theme" name="activate_theme" value="1"> Activate the theme after installing WordPress.</label>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="delete_default_themes">Default Themes</label>
                        </th>
                        <td colspan="2"><label><input type="checkbox" id="delete_default_themes" name="delete_default_themes" value="1"> Delete the default themes (Twenty Family).</label></td>
                    </tr>
                </tbody></table>

            <h1>Extensions Informations</h1>
            <p>Simply enter below the extensions that should be addend during the installation.</p>
            <table class="form-table">
                <tbody><tr>
                        <th scope="row">
                            <label for="plugins">Free Extensions</label>
                            <p>The extension slug is available in the url (Ex: http://wordpress.org/extend/plugins/<strong>wordpress-seo</strong>)</p>
                        </th>
                        <td>
                            <input name="plugins" type="text" id="plugins" size="50" value="">
                            <p>Make sure that the extensions slugs are separated by a semicolon (;).</p>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="plugins">Premium Extensions</label>
                            <p>Zip Archives have to be in the <em>plugins</em> folder at the <em>wp-quick-install</em> root folder.</p>
                        </th>
                        <td><label><input type="checkbox" id="plugins_premium" name="plugins_premium" value="1"> Install the premium extensions after WordPress installation.</label></td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="plugins">Automatic activation</label>
                        </th>
                        <td><label><input type="checkbox" name="activate_plugins" id="activate_plugins" value="1"> Activate the extensions after WordPress installation.</label></td>
                    </tr>
                </tbody></table>

            <h1>Permalinks Informations</h1>

            <p>By default WordPress uses web URLs which have question marks and lots of numbers in them; however, WordPress offers you the ability to create a custom URL structure for your permalinks and archives. This can improve the aesthetics, usability, and forward-compatibility of your links. A <a href="http://codex.wordpress.org/Using_Permalinks">number of tags are available</a>.</p>

            <table class="form-table">
                <tbody><tr>
                        <th scope="row">
                            <label for="permalink_structure">Custom Structure</label>
                        </th>
                        <td>
                            <code>http://localhost</code>
                            <input name="permalink_structure" type="text" id="permalink_structure" size="50" value="/%postname%/">
                        </td>
                    </tr>
                </tbody></table>

            <h1>Media Informations</h1>

            <p>Specified dimensions below determine the maximum dimensions (in pixels) to use when inserting an image into the body of an article.</p>

            <table class="form-table">
                <tbody><tr>
                        <th scope="row">Thumbnail sizes</th>
                        <td>
                            <label for="thumbnail_size_w">Width : </label>
                            <input name="thumbnail_size_w" style="width:100px;" type="number" id="thumbnail_size_w" min="0" step="10" value="0" size="1">
                            <label for="thumbnail_size_h">Height : </label>
                            <input name="thumbnail_size_h" style="width:100px;" type="number" id="thumbnail_size_h" min="0" step="10" value="0" size="1"><br>
                            <label for="thumbnail_crop" class="small-text"><input name="thumbnail_crop" type="checkbox" id="thumbnail_crop" value="1" checked="checked">Resize images to get the exact dimensions</label>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">Middle Size</th>
                        <td>
                            <label for="medium_size_w">Width :</label>
                            <input name="medium_size_w" style="width:100px;" type="number" id="medium_size_w" min="0" step="10" value="0" size="5">
                            <label for="medium_size_h">Height : </label>
                            <input name="medium_size_h" style="width:100px;" type="number" id="medium_size_h" min="0" step="10" value="0" size="5"><br>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">Big Size</th>
                        <td>
                            <label for="large_size_w">Width : </label>
                            <input name="large_size_w" style="width:100px;" type="number" id="large_size_w" min="0" step="10" value="0" size="5">
                            <label for="large_size_h">Height : </label>
                            <input name="large_size_h" style="width:100px;" type="number" id="large_size_h" min="0" step="10" value="0" size="5"><br>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="upload_dir">Store uploaded files in this folder</label>
                            <p>By default, medias are stored in the <em> wp-content/uploads</em> folder</p>
                        </th>
                        <td>
                            <input type="text" id="upload_dir" name="upload_dir" size="46" value=""><br>
                            <label for="uploads_use_yearmonth_folders" class="small-text"><input name="uploads_use_yearmonth_folders" type="checkbox" id="uploads_use_yearmonth_folders" value="1" checked="checked">Organize my files in monthly and annual folders</label>
                        </td>
                    </tr>
                </tbody></table>

            <h1>wp-config.php Informations</h1>
            <p>Choose below the additional constants you want to add in <strong>wp-config.php</strong></p>

            <table class="form-table">
                <tbody><tr>
                        <th scope="row">
                            <label for="post_revisions">Revisions</label>
                            <p>By default, number of post revision is unlimited</p>
                        </th>
                        <td>
                            <input name="post_revisions" id="post_revisions" type="number" min="0" value="0">
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="plugins">Editor</label>
                        </th>
                        <td><label><input type="checkbox" id="disallow_file_edit" name="disallow_file_edit" value="1" checked="checked">Disable theme and extensions editor</label></td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="autosave_interval">Autosave</label>
                            <p>By default, autosave interval is 60 seconds.</p>
                        </th>
                        <td><input name="autosave_interval" id="autosave_interval" type="number" min="60" step="60" size="25" value="7200"> seconds</td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="debug">Debug Mode</label>
                        </th>
                        <td>
                            <label><input type="checkbox" name="debug" id="debug" value="1"> Enable WordPress debug mode</label><p>By checking this box, WordPress will displaying errors</p>

                            <div id="debug_options" style="display:none;">
                                <label><input type="checkbox" name="debug_display" id="debug_display" value="1"> Enable WP Debug</label>
                                <br>
                                <label><input type="checkbox" name="debug_log" id="debug_log" value="1"> Write errors in a log file <em>(wp-content/debug.log)</em>. </label>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th scope="row">
                            <label for="wpcom_api_key">WP.com API Key</label>
                        </th>
                        <td><input name="wpcom_api_key" id="wpcom_api_key" type="text" size="25" value=""></td>
                    </tr>
                </tbody></table>

            <p class="step" style="display: flex; flex: 1; justify-content: center;">
                <input id="submit" class="login100-form-btn" type="submit" value="Install WordPress">
            </p>

        </form>
    </div>
</div>

<div id="waitDeploy" class="modal modelDialog">

    <div class="modal-content animate">
        <div class="imgcontainer">
            <label style="text-align: center; font-size: 3em;">Waiting Deployment Wordpress</label>
        </div>

        <div class="container" style='align-items: center; justify-content: center; flex: 1; display: flex;'>
                <div class="lds-ring"><div></div><div></div><div></div><div></div></div>
        </div>
    </div>
</div>