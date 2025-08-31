// Profile.tsx
import * as React from 'react';
import styles from './Profile.module.css';
import { UserResponse, ExperienceLevel, ExperienceLabels } from '../../../../types/user/response';
import { useGetUser } from '../../../../hooks/useUser';
import { Divider } from '@mui/material';
import { useState, useEffect, FormEvent } from 'react';
import PlacesAutocomplete, { geocodeByAddress } from 'react-places-autocomplete';
import { FaFacebook, FaLinkedin, FaGithub, FaInstagram, FaTwitter } from 'react-icons/fa';
import {
  useUpdateUser,
  useUpdateProfileImage,
  useRemoveProfileImage,
} from '../../../../hooks/useUser';
import { UpdateUserRequest } from '../../../../types/user/request';
import LoadingScreen from '../../../../features/LoadingScreen/LoadingScreen';
import ErrorMessage from '../../../../features/Snackbars/errorMessage';
import { validateEmail, validateRequired, validateUrl } from '../../../../utils/validationUtils';
import classNames from 'classnames';
import FallbackAvatars from '../../../common/Avatar';

interface ProfileProps {
  user?: UserResponse;
}

interface AddressComponent {
  long_name: string;
  short_name: string;
  types: string[];
}

export const Profile: React.FC<ProfileProps> = () => {
  const { data: user, isLoading, isError } = useGetUser();
  const updateUser = useUpdateUser();
  const updateProfileIcon = useUpdateProfileImage();
  const removeProfileIcon = useRemoveProfileImage();

  const [isSubmitted, setIsSubmitted] = useState(false);

  const [location, setLocation] = useState(user?.location);
  const [locationError, setLocationError] = useState('');
  const [selectedPlaceId, setSelectedPlaceId] = useState<string | null>(null);

  const [firstname, setFirstname] = useState('');
  const [firstnameError, setFirstnameError] = useState('');
  const [lastname, setLastname] = useState('');
  const [lastnameError, setLastnameError] = useState('');
  const [email, setEmail] = useState('');
  const [emailError, setEmailError] = useState('');
  const [summary, setSummary] = useState('');
  const [title, setTitle] = useState('');
  const [experience, setExperience] = useState(ExperienceLabels.NEW_BLOGGER);
  const [socials, setSocials] = useState({
    facebook: '',
    linkedin: '',
    github: '',
    instagram: '',
    twitter: '',
  });
  const [facebookError, setFacebookError] = useState('');
  const [linkedinError, setLinkedinError] = useState('');
  const [githubError, setGithubError] = useState('');
  const [instagramError, setInstagramError] = useState('');
  const [twitterError, setTwitterError] = useState('');
  const [selectedFile, setSelectedFile] = useState<File | 'Remove' | null>(null);

  const firstnameRef = React.useRef<HTMLInputElement>(null);
  const lastnameRef = React.useRef<HTMLInputElement>(null);
  const emailRef = React.useRef<HTMLInputElement>(null);
  const locationRef = React.useRef<HTMLInputElement>(null);
  const facebookRef = React.useRef<HTMLInputElement>(null);
  const linkedinRef = React.useRef<HTMLInputElement>(null);
  const githubRef = React.useRef<HTMLInputElement>(null);
  const instagramRef = React.useRef<HTMLInputElement>(null);
  const twitterRef = React.useRef<HTMLInputElement>(null);

  // Initialize form fields from user data
  useEffect(() => {
    if (user) {
      setFirstname(user.firstname ?? '');
      setLastname(user.lastname ?? '');
      setEmail(user.email ?? '');
      setSummary(user.summary ?? '');
      setLocation(user.location ?? '');
      setTitle(user.title ?? '');
      setExperience(user.experience ?? ExperienceLabels.NEW_BLOGGER);
      if (user.location) {
        setSelectedPlaceId('preset');
      }
      setExperience(user.experience || ExperienceLabels.NEW_BLOGGER);
      setSocials({
        facebook: user?.socials?.facebook || '',
        linkedin: user?.socials?.linkedin || '',
        github: user?.socials?.github || '',
        instagram: user?.socials?.instagram || '',
        twitter: user?.socials?.twitter || '',
      });
    }
  }, [user]);

  const handleSelect = async (address: string, placeId: string) => {
    setLocation(address);
    setSelectedPlaceId(placeId);
    setLocationError('');

    try {
      const results = await geocodeByAddress(address);

      if (results[0]) {
        const components = results[0].address_components as AddressComponent[];

        const city = components.find((c) => c.types.includes('locality'))?.long_name;
        const country = components.find((c) => c.types.includes('country'))?.long_name;

        const formattedLocation = `${city ?? ''}${city && country ? ', ' : ''}${country ?? ''}`;
        setLocation(formattedLocation);
      }
    } catch (error) {
      setLocationError('Unable to verify this location. Please try again.');
    }
  };

  const handleChange = (platform: string, value: string) => {
    setSocials((prev) => ({
      ...prev,
      [platform]: value,
    }));
  };

  // New: validate location (allows empty, checks text if not selected)
  const validateLocation = async (): Promise<boolean> => {
    if (!location) return true; // empty allowed

    if (selectedPlaceId && selectedPlaceId !== 'preset') return true; // already selected

    try {
      const results = await geocodeByAddress(location);

      if (results.length === 0) {
        setLocationError('Please select a valid city from the dropdown');
        return false;
      }

      const components = results[0].address_components as AddressComponent[];
      const city = components.find((c) => c.types.includes('locality'))?.long_name;
      const country = components.find((c) => c.types.includes('country'))?.long_name;

      if (!city || !country) {
        setLocationError('Please select a valid city from the dropdown');
        return false;
      }

      setLocation(`${city}, ${country}`);
      setSelectedPlaceId('validated');
      setLocationError('');
      return true;
    } catch (err) {
      setLocationError('Unable to verify this location. Please try again.');
      return false;
    }
  };

  const scrollToError = () => {
    if (firstnameError && firstnameRef.current) {
      firstnameRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      firstnameRef.current.focus();
      return;
    }
    if (lastnameError && lastnameRef.current) {
      lastnameRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      lastnameRef.current.focus();
      return;
    }
    if (emailError && emailRef.current) {
      emailRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      emailRef.current.focus();
      return;
    }
    if (locationError && locationRef.current) {
      locationRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      locationRef.current.focus();
      return;
    }
    if (facebookError && facebookRef.current) {
      facebookRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      facebookRef.current.focus();
      return;
    }
    if (linkedinError && linkedinRef.current) {
      linkedinRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      linkedinRef.current.focus();
      return;
    }
    if (githubError && githubRef.current) {
      githubRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      githubRef.current.focus();
      return;
    }
    if (instagramError && instagramRef.current) {
      instagramRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      instagramRef.current.focus();
      return;
    }
    if (twitterError && twitterRef.current) {
      twitterRef.current.scrollIntoView({ behavior: 'smooth', block: 'center' });
      twitterRef.current.focus();
      return;
    }
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsSubmitted(true);

    let hasError = false;

    if (!validateRequired(firstname)) {
      setFirstnameError('First name is required');
      hasError = true;
    } else {
      setFirstnameError('');
    }

    if (!validateRequired(lastname)) {
      setLastnameError('Last name is required');
      hasError = true;
    } else {
      setLastnameError('');
    }

    if (!validateEmail(email)) {
      setEmailError('Please enter a valid email address');
      hasError = true;
    } else {
      setEmailError('');
    }

    if (!validateUrl(socials.facebook)) {
      setFacebookError('Please enter a valid URL');
      hasError = true;
    } else {
      setFacebookError('');
    }

    if (!validateUrl(socials.linkedin)) {
      setLinkedinError('Please enter a valid URL');
      hasError = true;
    } else {
      setLinkedinError('');
    }

    if (!validateUrl(socials.github)) {
      setGithubError('Please enter a valid URL');
      hasError = true;
    } else {
      setGithubError('');
    }

    if (!validateUrl(socials.instagram)) {
      setInstagramError('Please enter a valid URL');
      hasError = true;
    } else {
      setInstagramError('');
    }

    if (!validateUrl(socials.twitter)) {
      setTwitterError('Please enter a valid URL');
      hasError = true;
    } else {
      setTwitterError('');
    }

    if (hasError) {
      scrollToError();
      return;
    }

    const isLocationValid = await validateLocation();
    if (!isLocationValid) return;

    const selectedLabel = experience;
    const experienceLevel = selectedLabel as ExperienceLevel;

    const userDetails: UpdateUserRequest = {
      firstname,
      lastname,
      email,
      provider: user!.provider,
      summary,
      location,
      title,
      experience: experienceLevel,
      socials,
    };

    try {
      if (selectedFile === 'Remove' && user?.profileIconUrl) {
        await removeProfileIcon.mutateAsync();
      }
      if (selectedFile instanceof File) {
        await updateProfileIcon.mutateAsync(selectedFile);
      }
      await updateUser.mutateAsync({ data: userDetails });
    } catch (err) {}
  };

  const errorMessage =
    (updateUser?.error as { message?: string })?.message ||
    (updateProfileIcon?.error as { message?: string })?.message ||
    (removeProfileIcon?.error as { message?: string })?.message ||
    'Something went wrong';

  if (isError || !user)
    return (
      <div className="container">
        <div className={styles.message}>Could not load data.</div>
      </div>
    );

  return (
    <LoadingScreen isLoading={updateUser.isPending}>
      <form className={styles.profile} onSubmit={handleSubmit}>
        <div className={styles.heading}>
          {' '}
          <div>
            {' '}
            <h5>Profile Details</h5> <p>You can change your profile details here seamlessly.</p>{' '}
          </div>{' '}
          <div>
            {' '}
            <button type="submit">Save</button>{' '}
          </div>{' '}
        </div>{' '}
        <Divider />
        {/* Profile Picture */}
        <div className={styles.text}>
          <div>
            <h6>Profile Pictue</h6>
            <p>
              Say cheese! This little circle is your chance to shine—or at least not look like a
              potato.
            </p>
          </div>
          <div className={styles.profileIcon}>
            <FallbackAvatars user={user} isProfile onFileSelect={setSelectedFile} />
          </div>
        </div>
        <Divider />
        {/* NAME */}
        <div className={styles.text}>
          <div>
            <h6>Name</h6>
            <p>
              This is your legendary name — the one everyone will call you by, so make it count!
            </p>
          </div>
          <div>
            <input
              type="text"
              id="firstname"
              name="firstname"
              ref={firstnameRef}
              value={firstname}
              onChange={(e) => setFirstname(e.target.value)}
              placeholder="Enter your first name"
              autoComplete="given-name"
              aria-label="First name"
              className={classNames({
                [styles.invalidField]: isSubmitted && firstnameError,
              })}
            />
            <div className={styles.errorCont}>
              {firstnameError && <p className={styles.errorText}>{firstnameError}</p>}
            </div>
            <input
              type="text"
              id="lastname"
              name="lastname"
              ref={lastnameRef}
              value={lastname}
              onChange={(e) => setLastname(e.target.value)}
              placeholder="Enter your last name"
              autoComplete="family-name"
              aria-label="Last name"
              className={classNames({
                [styles.invalidField]: isSubmitted && lastnameError,
              })}
            />
            <div className={styles.errorCont}>
              {lastnameError && <p className={styles.errorText}>{lastnameError}</p>}
            </div>
          </div>
        </div>
        <Divider />
        {/* EMAIL */}
        <div className={styles.text}>
          <div>
            <h6>Email</h6>
            <p>
              {' '}
              This is your magical inbox portal, the one that brings all the notifications,
              messages, and spam straight to you. Make sure it’s one you actually check!{' '}
            </p>
          </div>
          <div>
            <input
              type="email"
              id="email"
              name="email"
              ref={emailRef}
              value={email}
              placeholder="Enter your email"
              readOnly
              required
              autoComplete="email"
              aria-label="Email"
              className={classNames(styles.noTyping, {
                [styles.invalidField]: isSubmitted && emailError,
              })}
            />
            <div className={styles.errorCont}>
              {emailError && <p className={styles.errorText}>{emailError}</p>}
            </div>
          </div>
        </div>
        <Divider />
        {/* LOCATION */}
        <div className={styles.text}>
          <div>
            <h6>Location</h6>
            <p>
              {' '}
              Where in the world are you hiding? Your secret lair, favorite city, or that magical
              spot everyone dreams of visiting — share it with the world!{' '}
            </p>
          </div>
          <div className={styles.autocompleteWrapper}>
            <PlacesAutocomplete
              value={location ?? ''}
              onChange={(val: string) => {
                setLocation(val);
                setSelectedPlaceId(null);
              }}
              onSelect={handleSelect}
              searchOptions={{ types: ['(cities)'] }}
            >
              {({
                getInputProps,
                suggestions,
                getSuggestionItemProps,
                loading,
              }: {
                getInputProps: (options?: object) => React.InputHTMLAttributes<HTMLInputElement>;
                suggestions: {
                  active: boolean;
                  description: string;
                  placeId: string;
                }[];
                getSuggestionItemProps: (
                  suggestion: { active: boolean; description: string; placeId: string },
                  options?: object
                ) => React.HTMLAttributes<HTMLDivElement>;
                loading: boolean;
              }) => (
                <div className={styles.autocompleteContainer}>
                  <input
                    ref={locationRef}
                    {...getInputProps({
                      placeholder: 'Enter your city',
                      className: classNames(styles.locationInput, {
                        [styles.invalidField]: isSubmitted && locationError,
                      }),
                    })}
                  />
                  {(loading || suggestions.length > 0) && (
                    <div className={styles.suggestionsPopup}>
                      {loading && <div className={styles.suggestionItem}>Loading...</div>}
                      {suggestions.map((suggestion) => {
                        const props = getSuggestionItemProps(suggestion, {
                          className: styles.suggestionItem,
                          style: { backgroundColor: suggestion.active ? '#e0e0e0' : '#fff' },
                        });

                        return (
                          <div key={suggestion.placeId} {...props}>
                            {suggestion.description}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              )}
            </PlacesAutocomplete>

            <div className={styles.errorCont}>
              {locationError && <p className={styles.errorText}>{locationError}</p>}
            </div>
          </div>
        </div>
        <Divider />
        {/* TITLE */}
        <div className={styles.text}>
          <div>
            <h6>Title</h6>
            <p>
              What’s your badge of honor in the blogging world? Maybe you’re a Full-Stack Explorer,
              a Travel Storyteller, or something completely wild — make it yours and show off your
              unique style!
            </p>
          </div>
          <div>
            <input
              type="text"
              id="title"
              name="title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Enter your title"
              aria-label="Title"
            />
          </div>
        </div>
        <Divider />
        {/* EXPERIENCE */}
        <div className={styles.text}>
          <div>
            <h6>Experience</h6>
            <p>
              How seasoned are you in the blogging universe? Are you just starting your epic journey
              as a New Blogger, casually dropping posts, or already a Frequent Contributor shaping
              the community? Pick your level and let everyone know your blogging superpowers!
            </p>
          </div>
          <div>
            <select
              id="experience"
              name="experience"
              value={experience}
              onChange={(e) => setExperience(e.target.value as keyof typeof ExperienceLabels)}
              aria-label="Experience level"
            >
              {Object.entries(ExperienceLabels).map(([key, label]) => (
                <option key={key} value={key}>
                  {label}
                </option>
              ))}
            </select>
          </div>
        </div>
        <Divider />
        {/* SOCIALS */}
        <div className={styles.text}>
          <div>
            <h6>Socials</h6>
            <p>
              Time to show off your digital playground! Drop your Twitter, Instagram, or TikTok
              links — or maybe your secret cat meme account. Let the world follow your adventures,
              one post at a time!
            </p>
          </div>
          <div>
            <div className={styles.socials}>
              {/* Facebook */}
              <div className={styles.socialRow}>
                <FaFacebook className={styles.icon} />
                <div className={styles.socialsCont}>
                  <input
                    type="url"
                    ref={facebookRef}
                    placeholder="Facebook link"
                    value={socials.facebook}
                    onChange={(e) => handleChange('facebook', e.target.value)}
                    className={classNames({
                      [styles.invalidField]: isSubmitted && facebookError,
                    })}
                  />
                  <div className={styles.errorCont}>
                    {facebookError && <p className={styles.errorText}>{facebookError}</p>}
                  </div>
                </div>
              </div>

              {/* LinkedIn */}
              <div className={styles.socialRow}>
                <FaLinkedin className={styles.icon} />
                <div className={styles.socialsCont}>
                  <input
                    type="url"
                    ref={linkedinRef}
                    placeholder="LinkedIn link"
                    value={socials.linkedin}
                    onChange={(e) => handleChange('linkedin', e.target.value)}
                    className={classNames({
                      [styles.invalidField]: isSubmitted && linkedinError,
                    })}
                  />
                  <div className={styles.errorCont}>
                    {linkedinError && <p className={styles.errorText}>{linkedinError}</p>}
                  </div>
                </div>
              </div>

              {/* GitHub */}
              <div className={styles.socialRow}>
                <FaGithub className={styles.icon} />
                <div className={styles.socialsCont}>
                  <input
                    type="url"
                    ref={githubRef}
                    placeholder="GitHub link"
                    value={socials.github}
                    onChange={(e) => handleChange('github', e.target.value)}
                    className={classNames({
                      [styles.invalidField]: isSubmitted && githubError,
                    })}
                  />
                  <div className={styles.errorCont}>
                    {githubError && <p className={styles.errorText}>{githubError}</p>}
                  </div>
                </div>
              </div>

              {/* Instagram */}
              <div className={styles.socialRow}>
                <FaInstagram className={styles.icon} />
                <div className={styles.socialsCont}>
                  <input
                    type="url"
                    ref={instagramRef}
                    placeholder="Instagram link"
                    value={socials.instagram}
                    onChange={(e) => handleChange('instagram', e.target.value)}
                    className={classNames({
                      [styles.invalidField]: isSubmitted && instagramError,
                    })}
                  />
                  <div className={styles.errorCont}>
                    {instagramError && <p className={styles.errorText}>{instagramError}</p>}
                  </div>
                </div>
              </div>

              {/* Twitter */}
              <div className={styles.socialRow}>
                <FaTwitter className={styles.icon} />
                <div className={styles.socialsCont}>
                  <input
                    type="url"
                    ref={twitterRef}
                    placeholder="Twitter link"
                    value={socials.twitter}
                    onChange={(e) => handleChange('twitter', e.target.value)}
                    className={classNames({
                      [styles.invalidField]: isSubmitted && twitterError,
                    })}
                  />
                  <div className={styles.errorCont}>
                    {twitterError && <p className={styles.errorText}>{twitterError}</p>}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
      {(updateUser.isError || updateProfileIcon.isError || removeProfileIcon.isError) && (
        <ErrorMessage message={errorMessage} />
      )}
    </LoadingScreen>
  );
};
